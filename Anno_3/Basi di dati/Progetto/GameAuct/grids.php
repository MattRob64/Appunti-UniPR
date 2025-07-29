<?php include 'header.php'?>

    <?php 
    // Molto simile a index.php cambia solo che viene utilizzato per visualizzare tutti gli altri tipi di gallerie
    // Creato per non andare in conflitto con index
    $gallery_status = $_GET['gallery_status'];
    include 'gallery.php';?>

    <?php if ($gallery_status == 'user_aste'):?>
        <div class="user-container">
            <a class="logout-butt" href="new_asta.php">Avvia una nuova asta</a>
        </div>
    <?php endif;?>

<?php include 'footer.html'?>