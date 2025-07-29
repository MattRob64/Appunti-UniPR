<?php
    // Controlla se il metodo del form è post
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        //Controlla se l'utente vuole accedere o registrarsi e lancia la funziona appropriata
        if (isset($_POST['login'])) {
            handle_login();
        } elseif (isset($_POST['register'])) {
            handle_registration();
        }
    }

    //Funzione di gestione dell'accesso
    function handle_login() {
        //La funzione trim() rimuove tutti gli spazi superflui
        $email = trim($_POST['email']);
        $password = $_POST['password'];
    
        //Possibile miglioramento: passare direttamente il pdo già settato alla funzione
        $pdo = get_db_connection();
        //Preparazione della query
        //Utilizza metodo prepare e non query perchè più sicuro e moderno, inoltre se utilizzato bene può proteggere da SQL Injection
        $stmt = $pdo->prepare("SELECT idutente, pass FROM utente WHERE email = ?");
        //Esegue query con parametro
        $stmt->execute([$email]);
        //Salva risultato query in una variabile
        $user = $stmt->fetch();
    
        //Se la query restituise risultato e la password combacia viene eseguito l'accesso
        if ($user && password_verify($password, $user['pass'])) {
            $_SESSION['user_id'] = $user['idutente'];
            header("Location: user.php");
            exit;
        } else {
            $_SESSION['error'] = "Email o password non valida";
            header("Location: login.php");
            exit;
        }
    }

    //Funzione di gestione della registrazione al sito
    function handle_registration() {
        $email = trim($_POST['email']);
        $username = trim($_POST['username']);
        //Eseguo l'hash sulla password inserita dall'utente per motivi di sicurezza
        $password = password_hash($_POST['password'], PASSWORD_DEFAULT);
    
        //Possibile miglioramento: passare direttamente il pdo già settato alla funzione
        $pdo = get_db_connection();
        
        // Controllo se la mail esiste già
        $stmt = $pdo->prepare("SELECT idutente FROM utente WHERE email = ?");
        $stmt->execute([$email]);
        if ($stmt->fetch()) {
            $_SESSION['error'] = "Email già registrata. Per favore accedi.";
            header("Location: login.php");
            exit;
        }
    
        // Controllo se il nome utente esiste già
        $stmt = $pdo->prepare("SELECT idutente FROM utente WHERE nomeutente = ?");
        $stmt->execute([$username]);
        if ($stmt->fetch()) {
            $_SESSION['error'] = "Nome utente già in uso";
            header("Location: registration.php");
            exit;
        }
    
        // Inserimento nuovo utente
        try {
            $stmt = $pdo->prepare("INSERT INTO utente (nomeutente, email, pass) VALUES (?, ?, ?)");
            $stmt->execute([$username, $email, $password]);
            $_SESSION['success'] = "Registrazione completata! Per favore accedi";
            //Possibile miglioramento: accesso automatico
            header("Location: login.php");
            exit;
        } catch (PDOException $e) {
            $_SESSION['error'] = "Registrazione fallita: " . $e->getMessage();
            header("Location: registration.php");
            exit;
        }
    }
?>