<?php include 'header.php'?>
<?php
    $stmt = $pdo->query("
    SELECT nome 
    FROM prodotto 
    ");
    $prodotti = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $dataInizio = $oggi;

    // Handle form submissions
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $errorAsta = false;
        $inDb = false;
        $categoria = $_POST['categoria'];
        $titolo = $_POST['nomeAsta'];
        $desc = $_POST['descAsta'];
        $dataFine = $_POST['dataFine'];
        $baseAsta = $_POST['baseAsta'];
        $stato = $_POST['stato'];
        
        if ($_POST['selNomeProd'] == '-') {
            if (!empty(trim($_POST['nomeProd']))) {
                $nome = $_POST['nomeProd'];
                $nomiProdotti = array_map('trim', array_map('strtolower', array_column($prodotti, 'nome')));
                if (in_array(trim(strtolower($nome)), $nomiProdotti)) {
                    $_SESSION['errorProdName'] = "Il nome del prodotto inserito è già presente del database";
                    $errorAsta = true;
                }
            } else {
                $_SESSION['errorProdName'] = "Nome inserito non valido";
                $errorAsta = true;
            }
        } else {
            $nome = $_POST['selNomeProd'];
            $inDb = true;
        }

        if (empty(trim($titolo))) {
            $_SESSION['errorTitle'] = "Titolo asta inserito non valido";
            $errorAsta = true;
        }

        if (empty(trim($desc))) {
            $_SESSION['errorDesc'] = "Descrizione asta inserita non valida";
            $errorAsta = true;
        }

        if ($baseAsta < 0) {
            $_SESSION['errorPrezzo'] = "La base d'asta non può essere negativa";
            $errorAsta = true;
        } 

        if ($errorAsta) {
            header("Location: new_asta.php");
            exit;
        }

        //echo $dataInizio;

        if (empty($dataFine)) {
            $dataFine = date('Y-m-d', strtotime($dataInizio. ' + 30 days'));
        }

        //echo $dataFine;

        //Inserimento nuova asta
        try {
            if (!$inDb) {
                $stmt = $pdo->prepare("INSERT INTO prodotto (nome, categoria) VALUES (?, ?)");
                $stmt->execute([$nome, $categoria]);
            } else {
                $inDb = false;
            }
            
            $stmt = $pdo->prepare("
            SELECT idprodotto 
            FROM prodotto 
            WHERE nome = ?
            ");
            $stmt->execute([$nome]);
            $idprodotto = $stmt->fetchColumn(); // cerca nella prima colonna della prima riga

            $stmt = $pdo->prepare("INSERT INTO asta (idprodotto, idutentepubblicatore, titolo, descrizione, datainizio, datafine, baseprezzo, condizione) VALUES (?, ?, ? , ? , ? , ?, ?, ?)");
            $stmt->execute([$idprodotto, $_SESSION['user_id'], $titolo, $desc, $dataInizio, $dataFine, $baseAsta, $stato]);
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
            <?php if (isset($_SESSION['errorProdName'])): ?>
                <p class="error"><?= $_SESSION['errorProdName'] ?></p>
                <?php unset($_SESSION['errorProdName']); ?>
            <?php endif; ?>
            <?php if (isset($_SESSION['errorTitle'])): ?>
                <p class="error"><?= $_SESSION['errorTitle'] ?></p>
                <?php unset($_SESSION['errorTitle']); ?>
            <?php endif; ?>
            <?php if (isset($_SESSION['errorDesc'])): ?>
                <p class="error"><?= $_SESSION['errorDesc'] ?></p>
                <?php unset($_SESSION['errorDesc']); ?>
            <?php endif; ?>
            <?php if (isset($_SESSION['errorPrezzo'])): ?>
                <p class="error"><?= $_SESSION['errorPrezzo'] ?></p>
                <?php unset($_SESSION['errorPrezzo']); ?>
            <?php endif; ?>
            <?php if (isset($_SESSION['error'])): ?>
                <p class="error"><?= $_SESSION['error'] ?></p>
                <?php unset($_SESSION['error']); ?>
            <?php endif; ?>
            <form method="POST">
                <input type="hidden" name="new_prod" value="1">
                <p>
                    <label>Seleziona il nome del prodotto che vuoi inserire:</label><br>
                    <select name="selNomeProd">
                        <option value="-">-</option>
                        <?php foreach ($prodotti as $row): ?>
                            <option value="<?php echo $row['nome'] ?>"><?= htmlspecialchars($row['nome']) ?></option>
                        <?php endforeach; ?>
                    </select>
                </p>
                <p>
                    <label>(Se il prodotto non è in catalogo)Inserisci il nome del prodotto:</label><br>
                    <input type="text" name="nomeProd" >
                </p>
                <p>
                    <label>Inserisci la categoria del prodotto:</label><br>
                    <select name="categoria">
                        <option value="Console">Console</option>
                        <option value="Videogioco">Videogioco</option>
                    </select>
                </p>
                <p>
                    <label>Inserisci tag prodotto:(per ora disabilitato)</label><br>
                    <input type="text" name="tag" disabled>
                </p>
                <p>
                    <label>Inserisci immagini asta:(per ora disabilitato)</label><br>
                    <input type="image" name="immagini" disabled>
                </p>
                <p>
                    <label>Inserisci il titolo dell'asta:</label><br>
                    <input type="text" name="nomeAsta" required>
                </p>
                <p>
                    <label>Inserisci una breve descrizione dell'asta:</label><br>
                    <textarea type="text" name="descAsta" required></textarea>
                </p>
                <p>
                    <label>Inserisci la data di fine dell'asta(se non impostata il termine avverrà dopo 30 giorni dalla pubblicazione):</label><br>
                    <input type="date" name="dataFine" min="<?php echo $dataInizio; ?>">
                </p>
                <p>
                    <label>Inserisci il prezzo di base d'asta:</label><br>
                    <input type="number" name="baseAsta" required>
                </p>
                <p>
                    <label>In che condizione si trova il prodotto:</label><br>
                    <select name="stato">
                        <option value="Scarso">Scarso</option>
                        <option value="Discreto">Discreto</option>
                        <option value="Buono">Buono</option>
                        <option value="Eccellente">Eccellente</option>
                    </select>
                </p>
                <button type="submit">Avanti-></button>
            </form>
    </div>
<?php include 'footer.html'?>