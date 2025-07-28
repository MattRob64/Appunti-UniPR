<?php
    $gal_title = '';
    $aste = '';
    $idasta = '';
    switch($gallery_status) {
        case 'newest':
            $stmt = $pdo->prepare("SELECT idasta, titolo FROM asta WHERE datafine > ? ORDER BY datainizio LIMIT 20");
            $stmt->execute([$oggi]);
            $aste= $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Ultimi Arrivi';
            break;
        case 'user_aste':
            $stmt = $pdo->prepare("SELECT idasta, titolo FROM asta WHERE idutentepubblicatore = ? AND datafine > ? ORDER BY datainizio");
            //$stmt->bindValue('user_id', $_SESSION['user_id'], PDO::PARAM_INT);
            $stmt->execute([$_SESSION['user_id'], $oggi]);
            $aste = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Le Tue Aste';
            break;
        case 'gioco':
            $stmt = $pdo->prepare("SELECT idasta, titolo FROM asta JOIN prodotto ON asta.idprodotto = prodotto.idprodotto WHERE prodotto.categoria = ? AND datafine > ? ORDER BY datainizio");
            //$stmt->bindValue('user_id', $_SESSION['user_id'], PDO::PARAM_INT);
            $stmt->execute(['Videogioco', $oggi]);
            $aste = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Ultime Aste della categoria Videogiochi';
            break;
        case 'console':
            $stmt = $pdo->prepare("SELECT idasta, titolo FROM asta JOIN prodotto ON asta.idprodotto = prodotto.idprodotto WHERE prodotto.categoria = ? AND datafine > ? ORDER BY datainizio");
            //$stmt->bindValue('user_id', $_SESSION['user_id'], PDO::PARAM_INT);
            $stmt->execute(['Console', $oggi]);
            $aste = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Ultime Aste della categoria Console';
            break;
        case 'aste_old':
            $stmt = $pdo->prepare("SELECT idasta, titolo FROM asta WHERE datafine < ? ORDER BY datainizio LIMIT 20");
            $stmt->execute([$oggi]);
            $aste= $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Aste Terminate';
            break;
            break;
        case 'user_puntate':
            $stmt = $pdo->prepare("SELECT p.*, a.titolo AS titolo_asta, a.idasta AS idasta 
            FROM puntata p
            JOIN asta a ON p.idasta = a.idasta
            WHERE p.idutenteofferente = ? 
            ORDER BY p.datapuntata, p.orapuntata DESC
            ");
            $stmt->execute([$_SESSION['user_id']]);
            $puntate = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Le tue puntate';
            break;
    }
?>

<h1 class="titleBig"><?php echo $gal_title; ?></h1>

<?php if ($gallery_status == 'user_puntate'): ?>
    <div class="puntate-cont" >
        <?php if(empty($puntate)): ?>
            <div class="puntata-row" >
                <p class="dato-puntata">Attualmente non sono presenti puntate per questa asta</p>
            </div>
        <?php else:?>
            <?php foreach ($puntate as $row): ?>
                    <div class="puntata-row" >
                        <a class="dato-puntata" href="asta_page.php?idasta=<?php echo $row['idasta'];?>">Nome asta: <?php echo $row['titolo_asta'];?></a>
                        <p class="dato-puntata">Data Puntata: <?php echo date('d-m-Y', strtotime($row['datapuntata']));?></p>
                        <p class="dato-puntata">Ora Puntata: <?php echo $row['orapuntata'];?></p>
                        <p class="dato-puntata">Somma puntata:: <?php echo $row['sommapuntata'];?></p>
                    </div>
            <?php endforeach; ?>
        <?php endif;?>
    </div>
<?php else: ?>
    <div class="grid-container">
    <?php foreach ($aste as $row): ?>
    <a href="asta_page.php?idasta=<?php echo $row['idasta'];?>" class="grid-item">
        <img src="https://pbs.twimg.com/media/DT3l7ZsU8AEqSR3.jpg" >
        <div class="overlay"><?= htmlspecialchars($row['titolo']) ?></div>
    </a>
    <?php endforeach; ?>
    </div>
<?php endif; ?>

