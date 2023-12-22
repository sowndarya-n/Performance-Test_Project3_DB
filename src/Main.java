import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.*;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {
        // Define the connection parameters

        var actor = new Table("actor", "actor_id first_name last_name last_update",
                "Integer String String String", "actor_id");

        var film = new Table("film", "film_id title description release_year language_id original_language_id rental_duration rental_rate length replacement_cost rating last_update special_features fulltext", // fulltext",
                "Integer String String Integer Integer Integer Integer float Integer float String String String String", /*String",*/ "film_id");

        var film_select = new Table("film", "film_id title description release_year language_id original_language_id rental_duration rental_rate length replacement_cost rating last_update special_features fulltext", // fulltext",
                "Integer String String Integer Integer Integer Integer float Integer float String String String String", /*String",*/ "film_id");

        var film_actor = new Table("film_actor", "actor_id film_id last_updated",
                "Integer Integer String", "actor_id film_id");

        var customer = new Table("customer", "customer_id store_id first_name last_name email address_id activebool create_date last_update active",
                "Integer Integer String String String Integer Boolean Date String Integer", "customer_id");

        var inventory = new Table("inventory", "inventory_id film_id store_id last_update",
                "Integer Integer Integer String", "inventory_id");

        var rental = new Table("rental", "rental_id rental_date inventory_id customer_id return_date staff_id last_update",
                "Integer String Integer Integer String Integer String", "rental_id");

        fetchdata(actor);
        fetchdata(film);
        film.print();
        fetchdata(film_actor);
        fetchdata(customer);
        fetchdata(inventory);
        fetchdata(rental);



        //Start of query 1
        out.println ();
        long pre_time = System.nanoTime();
        var t_i_join = film.i_join ("film_id == film_id", film_actor);        // Indexed Join
        //var t_i_join = film.join ("film_id == film_id", film_actor);    //Nested Loop join
        long post_time = System.nanoTime();
        System.out.println(post_time - pre_time);
        //t_i_join.print();
        //End of Query1


        //Start of query 2
        out.println();
        pre_time = System.nanoTime();
        var t_join4 = rental.i_join("inventory_id == inventory_id", inventory); //Indexed Join
        //var t_join4 = rental.join("inventory_id == inventory_id", inventory); //Nested Loop join
        var t_join5 = t_join4.i_join("film_id == film_id",film);      //Indexed Join
        //var t_join5 = t_join4.join("film_id == film_id",film);  //Nested Loop join
        post_time = System.nanoTime();
        //t_join5.print();
        System.out.println(post_time - pre_time);
        // End of query 2

        //Start of Query 3
        out.println ();
        pre_time = System.nanoTime();
        var t_join3 = t_i_join.i_join ("actor_id == actor_id", actor);    //Indexed Join
        //var t_join3 = t_i_join.join ("actor_id == actor_id", actor); //Nested Loop join
        post_time = System.nanoTime();
        //t_join3.print ();
        System.out.println(post_time - pre_time);
        // End of Query 3


        //Start of Query 4
        out.println();
        pre_time = System.nanoTime();
        var t_join6 = t_join5.i_join("customer_id == customer_id", customer);     //Indexed join
        //var t_join6 = t_join5.join("customer_id == customer_id", customer); //Nested Loop join
        post_time = System.nanoTime();
        //t_join6.print();
        System.out.println(post_time - pre_time);
        //End of Query 4

        //query 5;
        pre_time = System.nanoTime();
       // var t_select2 = t_join3.select (t -> t[film.col("title")].equals ("CHICAGO NORTH")); // Non-Indexed Select
        var t_iselect2 = film.select (new KeyType ("141")); //Indexed select
        var t_sel_join1 = film_actor.i_join("film_id == film_id", t_iselect2);  //Indexed select
        var t_sel_join2 = t_sel_join1.i_join("actor_id == actor_id", actor);    //Indexed select
        //t_select2.print ();
        post_time = System.nanoTime();
        System.out.println(post_time - pre_time);

        //query 6
        pre_time = System.nanoTime();
        //var t_select3 = t_join5.select (t -> t[t_join5.col("title")].equals ("CHICAGO NORTH")); //Non-Indexed Select
        //var t_iselect2 = film.select (new KeyType ("141"));
        var t_sel_join3 = t_join4.i_join("film_id == film_id", t_iselect2);
        var t_sel_join4 = t_sel_join3.i_join("customer_id == customer_id", customer);
        //t_select3.print ();
        post_time = System.nanoTime();
        System.out.println(post_time - pre_time);
    }

    private static void fetchdata(Table table) {

        String jdbcUrl = "jdbc:postgresql://localhost:5432/pagila";
        String username = "postgres";
        String password = "root";
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            if (connection != null) {
                System.out.println("Connected to the PostgreSQL server!");

                // You can use the 'connection' object to perform database operations.
                String sqlQuery = "select * from "+ table.getName() +" LIMIT 2500";
                Statement statement = connection.createStatement();

                // Execute the query
                ResultSet resultSet = statement.executeQuery(sqlQuery);

                // Process the query results
                while (resultSet.next()) {

                    Comparable [] row = new Comparable[table.getattribute().length];

                    for(int i=1; i <= table.getattribute().length; ++i)
                    {
                        Object column = resultSet.getObject(i);
                        if(column instanceof Array)
                        {
                            Array list = (Array) column;
                            String strarr = Arrays.toString((Object[]) list.getArray());
                            row[i-1] = strarr;
                        }
                        else if(column instanceof String) {
                            row[i - 1] = ((String) column).trim();
                        }
                        else {
                            row[i - 1] = !Objects.isNull(column)  ? column.toString() : "";   // resultSet.getObject(i);
                        }
                    }
                    table.insert(row);
                   // String fname = resultSet.getString("first_name");
                   // String name = resultSet.getString("last_name");

                    // Do something with the retrieved data
                    //System.out.println(fname + " " + name);
                }
                System.out.println(String.format("Inserted %s records in table %s",table.records().size(),table.getName()));
                //table.print();
                // Close the resources
                resultSet.close();
                statement.close();
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Connection failed! Check the console for error details.");
            e.printStackTrace();
        }
    }
}


