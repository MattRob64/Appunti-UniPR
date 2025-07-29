<?php include 'header.php'?>

    <!--HomePage-->
    <?php 
    //Imposta gallery_status a newest, poi "avvia" gallery.php così da avere le ultime aste attive
    $gallery_status = 'newest';
    include 'gallery.php';?>

<?php include 'footer.html'?>