<?php include 'header.php'?>
<?php
    // Handle form submissions
    $edit = $_GET['edit'];
    $stmt = $pdo->prepare("SELECT biografia FROM utente WHERE idutente = ?");
    $stmt->execute([$_SESSION['user_id']]);
    $bio = $stmt->fetch();
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        switch ($edit) {
            case 1:
                // Check if email exists
                $stmt = $pdo->prepare("SELECT idutente FROM utente WHERE email = ?");
                $stmt->execute([$_POST['mail']]);
                if ($stmt->fetch()) {
                    $_SESSION['error'] = "Email già in uso, per favore immettine un'altra";
                    header("Location: edit_user.php?edit=1");
                    exit;
                }
                try {
                    $stmt = $pdo->prepare("UPDATE utente SET email = ? WHERE idutente = ?");
                    $stmt->execute([$_POST['mail'], $_SESSION['user_id']]);
                    $_SESSION['successNewAsta'] = "Email modificata con successo";
                    $edit = 0;
                    header("Location: user.php");
                    exit;
                } catch (PDOException $e) {
                    $_SESSION['error'] = "Qualcosa è andato storto: " . $e->getMessage();
                    header("Location: edit_user.php?edit=1");
                    exit;
                }
                break;
            case 2:
                try {
                    $stmt = $pdo->prepare("UPDATE utente SET biografia = ? WHERE idutente = ?");
                    $stmt->execute([$_POST['bio'], $_SESSION['user_id']]);
                    $_SESSION['successNewAsta'] = "Biografia modificata con successo";
                    $edit = 0;
                    header("Location: user.php");
                    exit;
                } catch (PDOException $e) {
                    $_SESSION['error'] = "Qualcosa è andato storto: " . $e->getMessage();
                    header("Location: edit_user.php?edit=2");
                    exit;
                }
                break;
            case 3:
                // Check if email exists
                $stmt = $pdo->prepare("SELECT idutente FROM utente WHERE nomeutente = ?");
                $stmt->execute([$_POST['user']]);
                if ($stmt->fetch()) {
                    $_SESSION['error'] = "Nome uente già in uso, per favore immettine un altro";
                    header("Location: edit_user.php?edit=3");
                    exit;
                }
                try {
                    $stmt = $pdo->prepare("UPDATE utente SET nomeutente = ? WHERE idutente = ?");
                    $stmt->execute([$_POST['user'], $_SESSION['user_id']]);
                    $_SESSION['successNewAsta'] = "Nome utente modificato con successo";
                    $edit = 0;
                    header("Location: user.php");
                    exit;
                } catch (PDOException $e) {
                    $_SESSION['error'] = "Qualcosa è andato storto: " . $e->getMessage();
                    header("Location: edit_user.php?edit=3");
                    exit;
                }
                break;
            case 4:
                break;
        }
        try {
            $stmt = $pdo->prepare("INSERT INTO prodotto (nome, categoria) VALUES (?, ?)");
            $stmt->execute([$nome, $categoria]);

            $_SESSION['successNewAsta'] = "Hai inserito una nuova asta con successo";
            header("Location: user.php");
            exit;
        } catch (PDOException $e) {
            $_SESSION['error'] = "Qualcosa è andato storto: " . $e->getMessage();
            header("Location: new_asta.php");
            exit;
        }
    }
?>
    <div class="login-container">
            <?php if (isset($_SESSION['error'])): ?>
                <p class="error"><?= $_SESSION['error'] ?></p>
                <?php unset($_SESSION['error']); ?>
            <?php endif; ?>
            <form method="POST">
                <input type="hidden" name="new_prod" value="1">
                <?php switch($edit): 
                    case 1:?>
                        <p>
                            <label>Inserisci la tua nuova email</label><br>
                            <input type="text" name="mail" >
                        </p>
                <?php break; ?>
                <?php case 2: ?>
                        <p>
                            <label>Modifica la tua biografia:</label><br>
                            <textarea type="text" name="bio" required><?php echo $bio['biografia']; ?></textarea>
                        </p>
                <?php break; ?>
                <?php case 3: ?>
                    <p>
                            <label>Inserisci il tuo nuovo username</label><br>
                            <input type="text" name="user" >
                    </p>
                <?php break; ?>
                <?php case 4: ?>
                    <p>
                            <label>Inserisci la vecchia password</label><br>
                            <input type="text" name="passOld" >
                            <label>Inserisci la nuova password</label><br>
                            <input type="text" name="passNew" >
                    </p>
                <?php break; ?>
                <?php endswitch; ?>
                <button type="submit">Conferma</button>
            </form>
    </div>
<?php include 'footer.html'?>