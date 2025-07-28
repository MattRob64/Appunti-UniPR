<?php include 'header.php'?>
<?php
    // Handle form submissions
    $edit = $_GET['edit'];
    $minasta = (float)$_GET['minasta'];
    $minpuntata = (float)$_GET['minpuntata'];
    $idasta = (int)$_GET['idasta'];
    //echo $ora;
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
                $stmt = $pdo->prepare("SELECT pass FROM utente WHERE idutente = ?");
                $stmt->execute([$_SESSION['user_id']]);
                $oldPass = $stmt->fetch(); //Utilizzare fetchColumn()
                if (!password_verify($_POST['passOld'], $oldPass['pass'])) {
                    $_SESSION['error'] = "La password vecchia inserita non corrisponde alla tua password";
                    header("Location: edit_user.php?edit=4");
                    exit;
                } else {
                    if (empty(trim($_POST['passNew']))) {
                        $_SESSION['error'] = "La nuova password inserita non è valida";
                        header("Location: edit_user.php?edit=4");
                        exit;
                    }
                    try {
                        $stmt = $pdo->prepare("UPDATE utente SET pass = ? WHERE idutente = ?");
                        $stmt->execute([password_hash($_POST['passNew'], PASSWORD_DEFAULT), $_SESSION['user_id']]);
                        $_SESSION['successNewAsta'] = "Password modificata con successo";
                        $edit = 0;
                        header("Location: user.php");
                        exit;
                    } catch (PDOException $e) {
                        $_SESSION['error'] = "Qualcosa è andato storto: " . $e->getMessage();
                        header("Location: edit_user.php?edit=4");
                        exit;
                    }
                }
                break;
            case 5:
                try {
                    $stmt = $pdo->prepare("INSERT INTO puntata (idasta, idutenteofferente, sommapuntata, datapuntata, orapuntata) VALUES (?, ?, ?, ?, ?)");
                    $stmt->execute([$idasta, $_SESSION['user_id'], $_POST['puntata'], $oggi, $ora]);
                    $_SESSION['successNewAsta'] = "Puntata effettuata con successo";
                    $edit = 0;
                    header("Location: user.php");
                    exit;
                } catch (PDOException $e) {
                    $_SESSION['error'] = "Qualcosa è andato storto: " . $e->getMessage();
                    header("Location: edit_user.php?edit=5");
                    exit;
                }
                break;
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
                            <input type="text" name="mail" required>
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
                            <input type="text" name="user" required>
                    </p>
                <?php break; ?>
                <?php case 4: ?>
                    <p>
                            <label>Inserisci la vecchia password</label><br>
                            <input type="text" name="passOld" required><br>
                            <label>Inserisci la nuova password</label><br>
                            <input type="text" name="passNew" required>
                    </p>
                <?php break; ?>
                <?php case 5: ?>
                    <p>
                            <label>Inserisci una somma da puntare per l'asta</label><br>
                            <?php if (!empty($minpuntata)): ?>
                                <input type="number" name="puntata" min="<?php echo $minpuntata; ?>"required><br>
                            <?php else: ?>
                                <input type="number" name="puntata" min="<?php echo $minasta;?>"required><br>
                            <?php endif; ?>
                    </p>
                <?php break; ?>
                <?php endswitch; ?>
                <button type="submit">Conferma</button>
            </form>
    </div>
<?php include 'footer.html'?>