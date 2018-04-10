package ma.ifdose;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

/*
import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;
*/
public class MyGenerator {

    public static void main(String[] args) {
        // Your app package name and the (.db) is the folder where the DAO files will be generated into.
        Schema schema = new Schema(1, "com.example.mylogin.db");
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try {
            new DaoGenerator().generateAll(schema, "./app/src/main/java");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema) {
        addUserEntities(schema);
        // addPhonesEntities(schema);
    }

    // This is use to describe the colums of your table
    private static Entity addUserEntities(final Schema schema) {
        Entity aliment = schema.addEntity("Aliment");
        aliment.addIdProperty().primaryKey().autoincrement();
//        aliment.addIntProperty("aliment_id").notNull();
        aliment.addStringProperty("name");
        aliment.addDoubleProperty("glucide");
        aliment.addDoubleProperty("quantite");
        aliment.addLongProperty("category_id");
        return aliment;
    }
//
    //    private static Entity addPhonesEntities(final Schema schema) {
    //        Entity phone = schema.addEntity("Phone");
    //        phone.addIdProperty().primaryKey().autoincrement();
    //        phone.addIntProperty("user_id").notNull();
    //        phone.addStringProperty("number");
    //        return phone;
    //    }

}

