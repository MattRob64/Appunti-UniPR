<?php
    $gal_title = '';
    $aste = '';
    switch($gallery_status) {
        case 'newest':
            $stmt = $pdo->prepare("SELECT titolo FROM asta WHERE datafine > ? ORDER BY datainizio LIMIT 20");
            $stmt->execute([$oggi]);
            $aste= $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Ultimi Arrivi';
            break;
        case 'user_aste':
            $stmt = $pdo->prepare("SELECT titolo FROM asta WHERE idutentepubblicatore = ? AND datafine > ? ORDER BY datainizio");
            //$stmt->bindValue('user_id', $_SESSION['user_id'], PDO::PARAM_INT);
            $stmt->execute([$_SESSION['user_id'], $oggi]);
            $aste = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Le Tue Aste';
            break;
        case 'gioco':
            $stmt = $pdo->prepare("SELECT titolo FROM asta JOIN prodotto ON asta.idprodotto = prodotto.idprodotto WHERE prodotto.categoria = ? AND datafine > ? ORDER BY datainizio");
            //$stmt->bindValue('user_id', $_SESSION['user_id'], PDO::PARAM_INT);
            $stmt->execute(['Videogioco', $oggi]);
            $aste = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Ultime Aste della categoria Videogiochi';
            break;
        case 'console':
            $stmt = $pdo->prepare("SELECT titolo FROM asta JOIN prodotto ON asta.idprodotto = prodotto.idprodotto WHERE prodotto.categoria = ? AND datafine > ? ORDER BY datainizio");
            //$stmt->bindValue('user_id', $_SESSION['user_id'], PDO::PARAM_INT);
            $stmt->execute(['Console', $oggi]);
            $aste = $stmt->fetchAll(PDO::FETCH_ASSOC);
            $gal_title = 'Ultime Aste della categoria Console';
            break;
    }
?>

<h1 class="titleBig"><?php echo $gal_title; ?></h1>

<div class="grid-container">
    <?php foreach ($aste as $row): ?>
    <a href="#" class="grid-item">
        <img src="https://pbs.twimg.com/media/DT3l7ZsU8AEqSR3.jpg" >
        <div class="overlay"><?= htmlspecialchars($row['titolo']) ?></div>
    </a>
    <?php endforeach; ?>
</div>
