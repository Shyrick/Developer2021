package lection2.Storage;

import lection2.Entity.Cat;
import lection2.Entity.Owner;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JDBCStorage {

    private String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private String SERVER_PATH = "localhost:3306"; // путь к серверу и порт (здесь порт 3306)
    private String DB_NAME = "fierstdb"; // в WorkBanch должна быть база данных с таким именем
    private  String DB_LOGIN = "root";
    private String DB_PASSWORD = "root";

    private Connection connection;
    private Statement st;

    private PreparedStatement createCatSt;
    private PreparedStatement updateCatSt;
    private PreparedStatement searchCatSt;
    private PreparedStatement selectCatsByOwnerId;

    private PreparedStatement createOwnerSt;
    private PreparedStatement selectOwnerSt;

    public JDBCStorage(){
        initDbDriver (); // В конструктое вызываем метод для инициализации драйвера
        initConnection();
        initPreparedStatement();
    }

    private void initDbDriver (){  //Метод для инициализации драйвера
        try {
            Class.forName(DB_DRIVER); // Создает объект указанного класса и зависывает в себя имя драйвера
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initConnection (){
        String connectionURL = "jdbc:mysql://" + SERVER_PATH + "/" + DB_NAME;
        connectionURL += "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        // без этого (?useUnicode=true&use...) выдает ошибку
        try {
            connection = DriverManager.getConnection(connectionURL, DB_LOGIN, DB_PASSWORD);
            st = connection.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initPreparedStatement(){
        try{
            createCatSt =  connection.prepareStatement("INSERT INTO Cat (cat_name, weight, sex) VALUES(?, ?, ?)");
            // Знак ? заменяет один параметр
            updateCatSt = connection.prepareStatement("UPDATE Cat SET cat_name=?, weight=?, sex=?, owner_id=? WHERE id=?");

            searchCatSt = connection.prepareStatement("SELECT id, cat_name, weight, sex, owner_id FROM Cat WHERE cat_name LIKE ?");

            createOwnerSt = connection.prepareStatement("INSERT INTO Owner(FIRST_NAME, LAST_NAME) VALUES (?, ?)");

            selectOwnerSt = connection.prepareStatement("SELECT first_name, last_name FROM Owner WHERE id=?");

            selectCatsByOwnerId = connection.prepareStatement("SELECT id, cat_name, weight, sex FROM Cat WHERE owner_id = ?");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Cat getCatById (long catId){
        // Пишем обычный SELECT как в WorkBanch
        String selectSql = "SELECT id, cat_name, weight, sex, owner_id FROM Cat " +
                "WHERE id=" + catId;
        ResultSet rs = null;    // Это очередь объектов из таблицы,
        // (данные в одной строке таблицы) по которой мы будем двигаться
        try {
            rs = st.executeQuery(selectSql); // выполняет запрос в SQL и получает данные из таблицы
            // запрос который возвращает данные
            if (rs.first()){ // медот first() возвращает true если есть первый объект, т.е если запрос выполнен успешно
                // и кот с таким id есть
                // rs.first() - указатель стоит в положенни 0 и если есть первый объект в очереди - возвращает true
                // и теперь можно по этим данным создать объект Cat
                Cat cat = new Cat();
                cat.setId(rs.getLong("id"));                // id и др. - это название столбцов таблицы (как в SQL запросе)
                cat.setCatName(rs.getString("cat_name"));   // см. String selectSql
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));
                cat.setOwnerId(rs.getLong("owner_id"));

                return cat;

            }else {
                return null; //возвращаем null если данных в таблице нет и или не правильный SQL-запрос
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeResultSet(rs); // в конце закрываем наш ResultSet. Делаем это в одельно написанном методе
        }                   // Это нужно, чтобы освободить память


    }

    private void closeResultSet (ResultSet rs){
        if (rs != null) {
            try {
                rs.close(); // Закрываем запрос (объект запроса) чтобы освободить память
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }  //Time 42:00



    public void createOwner(Owner owner){
        try{
            createOwnerSt.setString(1, owner.getFirstName());
            createOwnerSt.setString(2, owner.getLastName());
            createOwnerSt.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void setOwnerForCat(Cat cat, Owner owner){

        cat.setOwnerId(owner.getId());
        updateCat(cat);
    }

    public Owner getOwnerById(long id){
        ResultSet rs = null;
        try {
            selectOwnerSt.setLong(1, id);
            rs = selectOwnerSt.executeQuery();
            if (rs.first()){
                Owner owner = new Owner();
                owner.setId(id);
                owner.setFirstName(rs.getString("first_name"));
                owner.setLastName(rs.getString("last_name"));

                return owner;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeResultSet(rs);
        }
        return null;
    }





//    public void createCat (Cat cat){        // Медленный вариант метода
//        try {
//            createCatSt.setString(1, cat.getCatName()); // Это вместо знаков ? - в метод соответсвующего типа
//            // передаем номер параметра и его значение, при этом в sql-запросе все varchar (стринг) будут экранированныы автоматом
//            createCatSt.setFloat(2, cat.getWeight());
//            createCatSt.setBoolean(3, cat.getSex());
//
//            createCatSt.executeUpdate();
//            cat.setId(getLastCatid());
//
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
////
////      Первый вариант метода без PreparedStatement
//
////      String sql = "INSERT INTO Cat (cat_name, weight, sex) VALUES (" +
////                "'" + cat.getCatName() + "', " +cat.getWeight() + ", " + cat.getSex() + ")";
////        try {
////            st.executeUpdate(sql); //выполняет sql код без возвращения результата
////            cat.setId(getLastCatid());
////
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
//    }
//
//    public void createCats (List<Cat> cats){ // Метод создает сразу много котов из списка
//        // Медленный вариант метода
//
//        for (Cat cat:cats) {
//            createCat(cat);
//        }
//    }

    private long getLastCatid (){
        // Метод нужен для того, что бы при создании объекта Cat у него был id,
        // который генерирует WorkBench автоматически, мы его не задаем и при создании Кота не знаем

        String sql ="SELECT id FROM Cat ORDER BY id DESC LIMIT 1";
        ResultSet rs = null;
        try {
            rs =st.executeQuery(sql);

            if (rs.first()){

                return rs.getLong("id");
            }else{
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }finally {
            closeResultSet(rs);
        }

    }

    public void createCats(List<Cat> cats) throws SQLException {
        connection.setAutoCommit(false);

        for (Cat cat : cats) {

            createCatSt.setString(1, cat.getCatName());
            createCatSt.setFloat(2, cat.getWeight());
            createCatSt.setBoolean(3, cat.getSex());

            createCatSt.addBatch();        // добавление SQL-запроса в очередь на выполнение, но без выполнения
        }
        // createCatSt.executeUpdate(); // выполнение SQL запроса
        createCatSt.executeBatch(); // Выполнение очереди запросов

        connection.setAutoCommit(true) ;
    }

    public void deleteCatById (long id){
        String sql = "DELETE FROM Cat WHERE id=" + id;

        try {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCat(Cat cat){     // Медленный вариант метода
        try{
            updateCatSt.setString(1, cat.getCatName());
            updateCatSt.setFloat(2, cat.getWeight());
            updateCatSt.setBoolean(3, cat.getSex());
            updateCatSt.setLong(4, cat.getOwnerId());
            updateCatSt.setLong(5, cat.getId());

            updateCatSt.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
//    public void updateCats(List<Cat> cats){
//        for(Cat cat: cats){
//            updateCat(cat);
//        }
//    }

    public void updateCats(List<Cat> cats) throws SQLException {
        connection.setAutoCommit(false);
        for(Cat cat: cats){
            updateCatSt.setString(1, cat.getCatName());
            updateCatSt.setFloat(2, cat.getWeight());
            updateCatSt.setBoolean(3, cat.getSex());
            updateCatSt.setLong(4, cat.getOwnerId());
            updateCatSt.setLong(5, cat.getId());
            updateCatSt.addBatch();
        }

        updateCatSt.executeBatch();

        connection.setAutoCommit(true);
    }

    public void clear(){
        String sql = "DELETE FROM Cat";

        try{
            st.executeUpdate(sql);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public List<Cat> getAlCats(){
        List<Cat> result = new ArrayList<Cat>();

        String sql = "SELECT id, cat_name, weight, sex, owner_id FROM Cat";

        ResultSet rs = null;

        try{
            rs=st.executeQuery(sql);
            while (rs.next()){
                Cat cat = new Cat();
                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));
                cat.setOwnerId(rs.getLong("owner_id"));

                result.add(cat);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closeResultSet(rs);
        }
     return result;
    }

    public List<Cat> getAllCatsForOwner (Owner owner){
        List<Cat> result = new ArrayList<Cat>();

        ResultSet rs = null;
        try {
            selectCatsByOwnerId.setLong(1, owner.getId());

            rs = selectCatsByOwnerId.executeQuery();

            while ( rs.next()){
                Cat cat = new Cat();
                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));
                cat.setOwnerId(owner.getId());

                result.add(cat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Cat> search (String part){      // поиск кота по имени целиком или по части имени
        List<Cat> result = new ArrayList<Cat>();

        ResultSet rs = null;

        try{
            searchCatSt.setString(1, "%" + part + "%");
            rs = searchCatSt.executeQuery();

            while (rs.next()){
                Cat cat = new Cat();
                cat.setId(rs.getLong("id"));
                cat.setCatName(rs.getString("cat_name"));
                cat.setWeight(rs.getFloat("weight"));
                cat.setSex(rs.getBoolean("sex"));
                cat.setOwnerId(rs.getLong("owner_id"));


                result.add(cat);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closeResultSet(rs);
        }
        return result;
    }



    private static final String [] CAT_NAMES ={  // Массив имен для создания случайных котов
      "CatName1",
      "CatName2",
      "CatName3",
      "CatName4"
    };

    private Cat randomCat(){
        Random r = new Random();

        String catName = CAT_NAMES[r.nextInt(CAT_NAMES.length)];
        Cat cat = new Cat();
        cat.setCatName(catName);
        cat.setWeight(r.nextFloat());
        cat.setSex(r.nextBoolean());
        return cat;
     }

     public void deleteOwner(Owner owner){

        String deleteSql = "Delete from Owner WHERE owner.id=" + owner.getId();

        try{

            st.executeUpdate(deleteSql);

        } catch (Exception e){
            e.printStackTrace();
        }



     }

    public static void main(String[] args) throws SQLException {
        JDBCStorage storage = new JDBCStorage();

   //     storage.deleteOwner(storage.getOwnerById(4));

//        Owner owner = storage.getOwnerById(1);
//        List<Cat> allCats = storage.getAllCatsForOwner(owner);
//        for (Cat cat:allCats) {
//            System.out.println(cat);
//        }

//        Owner owner = storage.getOwnerById(1);
        Cat cat = storage.getCatById(5);
        System.out.println(cat);
//        storage.setOwnerForCat(cat, owner);

//        Owner owner = new Owner();
//        owner.setFirstName("Максим");
//        owner.setLastName("Федоров");
//        storage.createOwner(owner);


//        List<Cat> catsToAdd = new ArrayList<Cat>();
//
//        for (int i = 0; i <100 ; i++) {
//            catsToAdd.add(storage.randomCat());
//        }
//
//        List<Cat> allCats = storage.getAlCats();
//        long before = System.currentTimeMillis();
//               storage.updateCats(allCats);
////            storage.updateCats(allCats);
////            storage.createCats(catsToAdd);
////            storage.createCats(catsToAdd);
//        long after = System.currentTimeMillis();
//        long deltaTime = before - after;
//        System.out.println("Time is:  " + deltaTime + " ms");


        //List<Cat> cats = storage.search("rch");

//        List<Cat> cats = storage.getAlCats();
        //System.out.println("Cat counts: " + cats.size());

//        for (Cat cat:cats) {
//            System.out.println(cat);
//        }

//        storage.deleteCatById(5);

//        Cat cat = new Cat();
//        cat.setCatName("AnatherName");
//        cat.setWeight(5);
//        cat.setSex(true);//
//        storage.createCat(cat);

//        Cat cat = storage.getCatById(7);
//        cat.setCatName("Myrchick");
//        storage.updateCat(cat);
//        System.out.println(cat.getId());

//        Cat cat =storage.getCatById(2);
//        System.out.println(cat);
    }
}
