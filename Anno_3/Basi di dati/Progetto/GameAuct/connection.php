<?php

function get_db_connection() {
    $host = "localhost";
    $port = "5432";
    $dbname = "gameauctdb";
    $user = "postgres";
    $password = "Postgres25";

    $dsn = "pgsql:host=$host;port=$port;dbname=$dbname;user=$user;password=$password";
    try {
        $pdo = new PDO($dsn);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return $pdo;
    } catch (PDOException $e) {
        die("Database connection failed: " . $e->getMessage());
    }
}

//Vecchio metodo di connessione
//$connection = pg_connect("host=localhost dbname=gameauctdb user=postgres password=Postgres25");
    /*if(!$connection) {
        echo "An Error Has Occured <br>";
        exit;
    } else {
        echo "Connection Enstablished <br>";
    }*/
?>