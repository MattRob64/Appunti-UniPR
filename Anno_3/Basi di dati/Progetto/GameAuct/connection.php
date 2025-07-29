<?php

function get_db_connection() {
    //Impostazione parametri per connessione al database
    $host = "localhost";
    $port = "5432";
    $dbname = "gameauctdb";
    $user = "postgres";
    $password = "Postgres25";
    //Fatto come parametri e non coem stringa unica per migliorare scalabilità e adattabilità

    //Creazione "stringa" di connessione
    $dsn = "pgsql:host=$host;port=$port;dbname=$dbname;user=$user;password=$password";
    try {
        //Creazione PHP Data Object
        $pdo = new PDO($dsn);
        //Impostazione attributi di errore PDO
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