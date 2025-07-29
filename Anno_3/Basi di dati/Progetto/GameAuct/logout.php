<?php
    //Ottiene tutti i dati di sessione
    session_start();
    //Distrugge tutti i dati di sessione
    session_destroy();
    //Riporta l'utente alla pagina di login
    header("Location: login.php");
    exit;
?>