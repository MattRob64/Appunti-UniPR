<?php include 'header.php'?>

    <!--HomePage-->
    <?php 
    $gallery_status = 'newest';
    include 'gallery.php';?>
    <!-- <table>
        <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>Mail</th>
            <th>Bio</th>
            <th>Avatar</th>
        </tr>

        <?php
        /*while($row = pg_fetch_assoc($result)) {
            echo "
            <tr>
                <td>$row[idutente]</td>
                <td>$row[nomeutente]</td>
                <td>$row[email]</td>
                <td>$row[biografia]</td>
                <td>$row[avatar]</td>
            </tr>
            ";
        }*/
        ?>
    </table> -->

<?php include 'footer.html'?>