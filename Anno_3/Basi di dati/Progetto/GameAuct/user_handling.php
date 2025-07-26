<?php
    // Handle form submissions
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        if (isset($_POST['login'])) {
            handle_login();
        } elseif (isset($_POST['register'])) {
            handle_registration();
        }
    }

    function handle_login() {
        $email = trim($_POST['email']);
        $password = $_POST['password'];
    
        $pdo = get_db_connection();
        $stmt = $pdo->prepare("SELECT idutente, pass FROM utente WHERE email = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch();
    
        if ($user && password_verify($password, $user['pass'])) {
            $_SESSION['user_id'] = $user['idutente'];
            header("Location: user.php");
            exit;
        } else {
            $_SESSION['error'] = "Invalid email or password";
            header("Location: login.php");
            exit;
        }
    }

    function handle_registration() {
        $email = trim($_POST['email']);
        $username = trim($_POST['username']);
        $password = password_hash($_POST['password'], PASSWORD_DEFAULT);
    
        $pdo = get_db_connection();
        
        // Check if email exists
        $stmt = $pdo->prepare("SELECT idutente FROM utente WHERE email = ?");
        $stmt->execute([$email]);
        if ($stmt->fetch()) {
            $_SESSION['error'] = "Email already registered. Please login.";
            header("Location: login.php");
            exit;
        }
    
        // Check if username exists
        $stmt = $pdo->prepare("SELECT idutente FROM utente WHERE nomeutente = ?");
        $stmt->execute([$username]);
        if ($stmt->fetch()) {
            $_SESSION['error'] = "Username already taken";
            header("Location: registration.php");
            exit;
        }
    
        // Insert new user
        try {
            $stmt = $pdo->prepare("INSERT INTO utente (nomeutente, email, pass) VALUES (?, ?, ?)");
            $stmt->execute([$username, $email, $password]);
            $_SESSION['success'] = "Registration successful! Please login";
            header("Location: login.php");
            exit;
        } catch (PDOException $e) {
            $_SESSION['error'] = "Registration failed: " . $e->getMessage();
            header("Location: registration.php");
            exit;
        }
    }
?>