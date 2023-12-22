# Project- 3 - Database Management (CSCI 6370)

# Project Overview:
In the context of this project, we aim to test and compare the performance/speed (ms per query) of the Project 2 code. The test is done with NoMap, TreeMap, HashMap, and LinHashMap using the PostgreSQL and MySQL.

# Linear Hashing:
It is a flexible and disk-based indexing technique that utilizes hashing to manage data efficiently. It can adapt to changing data sizes by adjusting the number of buckets it uses, and its strength lies in its ability to quickly find records with specific keys, making it a valuable tool for database management and information retrieval systems.


# Instructions for code execution:

1. Download the PostgreSQL JDBC Driver from the official PostgreSQL website(https://jdbc.postgresql.org/download.html) or from the Maven Central Repository. 
2. If we are not using a build tool like Maven, we need to manually download the JDBC driver JAR file and add it to the project's classpath (This is done by copying the JAR file to a "lib" or "libs" directory in the project and then configuring your IDE to include this in the classpath).
3. Using JDBC connect to the Postgres SQL pagila database.
4. To check unindexed queries, we comment the queries that are in the nested loops.
5. To compile the code, execute the following command in the terminal:
   javac Main.java
6. If compilation encounters an error, an error message will be displayed in the command prompt.
7. Upon successful compilation, run the code using the following command:
   java Main
8. Successful execution will give us the execution time of each query.
9. To execute the set_of_queries.sql file, we use the command, "source local_storage_filepath" in my sql terminal or postgres query tool.

# Bonus Note:
The average time taken to execute all the queries is 7ms.


# Team Members:
V H Sowndarya Nookala (Manager)
Rohith Lingala
Krishna Chaitanya Velagapudi
Bavesh Chowdary Kamma 
Subhasree Nadimpalli

# Conclusion:
We completed the project within the deadline by following the effective task segregation, testing and evaluation of the operators by ensuring that they perform their intended functions accurately. 