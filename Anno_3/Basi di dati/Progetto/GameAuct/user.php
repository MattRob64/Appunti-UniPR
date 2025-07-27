<?php include 'header.php'?>
<?php
    /** $edit variabile di stato con 4 stati
     * 1: permette di cambiare la email
     * 2: permette di cambiare o aggiungere la bio
     * 3: permette di cambiare il nome utente
     * 4: permette di modificare la password
     * */
    $edit = 0;
    $stmt = $pdo->query("
    SELECT nomeutente, email, biografia, avatar 
    FROM utente 
    WHERE idutente = " . $_SESSION['user_id']
    . "");
    $dati_utente = $stmt->fetch(PDO::FETCH_ASSOC);
?>
    <div class="user-container">
        <?php if (isset($_SESSION['successNewAsta'])): ?>
            <p class="error"><?= $_SESSION['successNewAsta'] ?></p>
            <?php unset($_SESSION['successNewAsta']); ?>
        <?php endif; ?>
        <h1>Benvenuto nel tuo account <?php echo $dati_utente['nomeutente']; ?></h1>
        <img class="user-avatar" src="http://goo.gl/ijai22" />
        <a href="#">Cambia avatar</a>
        <p>La tua e-mail: <?php echo $dati_utente['email']; ?> <a href="edit_user.php?edit=1">Cambia la tua email</a> </p>
        <?php if (!is_null($dati_utente['biografia'])): ?>
            <h2>La tua biografia:</h2>
            <p class="bio"><?php echo $dati_utente['biografia']; ?></p>
            <a class="logout-butt" href="edit_user.php?edit=2">Modifica biografia</a>
        <?php else: ?>
            <a class="logout-butt" href="edit_user.php?edit=2">Aggiungi biografia</a>
        <?php endif; ?>
        <a class="logout-butt" href="edit_user.php?edit=3">Modifica Nome Utente</a>
        <a class="logout-butt" href="edit_user.php?edit=4">Modifica Password</a>
        <a class="logout-butt" href="grids.php?gallery_status=user_aste">Le tue aste</a>
        <a class="logout-butt" href="#">Le tue puntate</a>
        <a class="logout-butt" href="logout.php">Logout</a>
    </div>
<?php include 'footer.html'?>