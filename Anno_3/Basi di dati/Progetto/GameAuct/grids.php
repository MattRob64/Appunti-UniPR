<?php include 'header.php'?>

    <?php 
    $gallery_status = $_GET['gallery_status'];
    include 'gallery.php';?>

    <?php if ($gallery_status == 'user_aste'):?>
        <div class="user-container">
            <a class="logout-butt" href="new_asta.php">Avvia una nuova asta</a>
        </div>
    <?php endif;?>

<?php include 'footer.html'?>