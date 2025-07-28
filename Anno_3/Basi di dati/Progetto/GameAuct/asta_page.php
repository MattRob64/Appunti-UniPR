<?php include 'header.php'?>
<?php
    $idasta = $_GET['idasta'];
    //echo $idasta;
    $stmt = $pdo->prepare("SELECT a.*, p.nome AS nomeprodotto, p.categoria AS tipologia, u.nomeutente AS pubblicatore 
    FROM asta a 
    JOIN prodotto p ON a.idprodotto = p.idprodotto 
    JOIN utente u ON a.idutentepubblicatore = u.idutente 
    WHERE a.idasta = ?");
    $stmt->execute([$idasta]);
    $datiAsta = $stmt->fetch();

    $stmt = $pdo->prepare("SELECT p.*, u.nomeutente AS offerente FROM puntata p JOIN utente u ON p.idutenteofferente = u.idutente WHERE p.idasta = ? ORDER BY p.datapuntata, p.orapuntata DESC");
    $stmt->execute([$idasta]);
    $puntate = $stmt->fetchAll(PDO::FETCH_ASSOC);

    $stmt = $pdo->prepare("SELECT i.path AS path 
    FROM img_asta ia 
    JOIN immagine i ON ia.idimg = i.idimmagine 
    JOIN asta a on ia.idasta = a.idasta 
    WHERE ia.idasta = ?");
    $stmt->execute([$idasta]);
    $immagini = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>
    <h1 class="titleBig"><?php echo $datiAsta['titolo'];?></h1>
    <?php if($datiAsta['datafine'] < $oggi): ?>
        <p style="text-align: center;" class="error">Questa Asta È Terminata</p>
    <?php endif; ?>
    <div class="asta-page-cont">
        <div class="container-contenuti">
            <div class="list-container">
                <!-- Indicator Dots -->
                <div class="indicators"></div>
                <ul class="list">
                    <?php if(empty($immagini)): ?>
                        <li class="item"><img class="carous-image" src="https://via.placeholder.com/200" alt="image/jpeg"></li>
                    <?php else: ?>
                        <?php foreach ($immagini as $row): ?>
                            <li class="item"><img class="carous-image" src="<?php echo $row['path'];?>" alt="image/jpeg" ></li>
                        <?php endforeach;?>
                    <?php endif;?>
                </ul>
                <button onclick="handleClick('previous')" class="button button--previous" type="button"><i class="fa-solid fa-hand-point-left"></i></button>
                <button onclick="handleClick('next')" class="button button--next" type="button"><i class="fa-solid fa-hand-point-right"></i></button>
            </div>

            <ul class="dati-asta">
                <li>
                    <p>Nome prodotto: <?php echo $datiAsta['nomeprodotto'];?></p>
                </li>
                <li>
                    <p>Categoria prodotto: <?php echo $datiAsta['tipologia'];?></p>
                </li>
                <li>
                    <p>Descrizione:</p>
                    <p><?php echo $datiAsta['descrizione'];?></p>
                </li>
                <li>
                    <a href="#" >Pubblicato da: <?php echo $datiAsta['pubblicatore'];?></a>
                </li>
                <li>
                    <p>Asta pubblicata il: <?php echo date('d-m-Y', strtotime($datiAsta['datainizio']));?></p>
                </li>
                <li>
                    <?php if($datiAsta['datafine'] < $oggi): ?>
                        <p>L'asta si è conclusa il: <?php echo date('d-m-Y', strtotime($datiAsta['datafine']));?></p>
                    <?php else: ?>
                        <p>L'asta si concluderà il: <?php echo date('d-m-Y', strtotime($datiAsta['datafine']));?></p>
                    <?php endif; ?>
                </li>
                <li>
                    <p>Condizione prodotto: <?php echo $datiAsta['condizione'];?></p>
                </li>
                <li>
                    <p>Prezzo base d'asta: <?php echo $datiAsta['baseprezzo'];?>€</p>
                </li>
                <li>
                    <?php if (!empty($puntate[0]['sommapuntata'])): ?>
                        <?php if($datiAsta['datafine'] < $oggi): ?>
                            <p>L'asta è terminata con il seguente prezzo: <?php echo $puntate[0]['sommapuntata'];?>€</p>
                        <?php else: ?>
                            <p>Prezzo attuale: <?php echo $puntate[0]['sommapuntata'];?>€</p>
                        <?php endif; ?>
                    <?php else: ?>
                        <?php if($datiAsta['datafine'] < $oggi): ?>
                            <p>L'asta è terminata con il seguente prezzo: <?php echo $datiAsta['baseprezzo'];?>€</p>
                        <?php else: ?>
                            <p>Prezzo attuale: <?php echo $datiAsta['baseprezzo'];?>€</p>
                        <?php endif; ?>
                    <?php endif; ?>
                </li>
                <li style="margin-top: 40px;" >
                    <?php if($datiAsta['datafine'] > $oggi): ?>
                        <?php if (isset($_SESSION['user_id'])):?>
                            <a class="logout-butt" href="edit_user.php?edit=5&minasta=<?php echo $datiAsta['baseprezzo'];?>&minpuntata=<?php echo $puntate[0]['sommapuntata'];?>&idasta=<?php echo $datiAsta['idasta'];?>">Piazza una puntata</a>
                        <?php else:?>
                            <p style="margin-bottom: 40px;" >Per fare una puntata devi essere registrato</p>
                            <a class="logout-butt" href="login.php">Login</a>
                        <?php endif;?>
                    <?php endif;?>
                </li>
                <?php if($datiAsta['datafine'] < $oggi): ?>
                    <?php if(empty($puntate)): ?>
                        <li>
                            <p>Questo oggetto è rimasto invenduto</p>
                        </li>
                    <?php else:?>
                        <li>
                            <p>Questo oggetto se lo è aggiudicato: <?php echo $puntate[0]['offerente'];?></p>
                        </li>
                    <?php endif; ?>
                <?php endif; ?>
            </ul>
        </div>
    </div>

    <h1 class="titleBig">Storico puntate</h1>
    <div class="puntate-cont" >
        <?php if(empty($puntate)): ?>
            <div class="puntata-row" >
                <p class="dato-puntata">Attualmente non sono presenti puntate per questa asta</p>
            </div>
        <?php else:?>
            <?php foreach ($puntate as $row): ?>
                    <div class="puntata-row" >
                        <p class="dato-puntata">Data Puntata: <?php echo date('d-m-Y', strtotime($row['datapuntata']));?></p>
                        <p class="dato-puntata">Ora Puntata: <?php echo $row['orapuntata'];?></p>
                        <p class="dato-puntata">Fatta da: <?php echo $row['offerente'];?></p>
                        <p class="dato-puntata">Somma puntata:: <?php echo $row['sommapuntata'];?></p>
                    </div>
            <?php endforeach; ?>
        <?php endif;?>
    </div>

    <?php if (isset($_SESSION['user_id']) && $_SESSION['user_id'] == $datiAsta['idutentepubblicatore'] && $datiAsta['datafine'] > $oggi):?>
        <div class="user-container">
            <a class="logout-butt" href="#">Modifica la tua asta</a>
        </div>
    <?php endif;?>

    <script>
        const list = document.querySelector(".list");
        const items = document.querySelectorAll(".item");

        // We want to know the width of one of the items. 
        // We'll use this to decide how many pixels we want our carousel to scroll.
        const item = document.querySelector(".item");
        const itemHeight = item.offsetHeight;

        // Initialize the current index
        let currentIndex = 0;
            
        // Create indicators
        const indicatorsContainer = document.querySelector(".indicators");
        items.forEach((_, index) => {
            const indicator = document.createElement("span");
            indicator.classList.add("indicator");
            if (index === 0) {
                // Mark the first as active initially
                indicator.classList.add("active"); 
            }
            indicatorsContainer.appendChild(indicator);
        });

        // Set the current index based on scroll position after loading
        function setCurrentIndexFromScroll() {
            const scrollTop = list.scrollTop;
            currentIndex = Math.round(scrollTop / itemHeight);
            updateIndicators();
        }

        // Update indicators based on the current index
        function updateIndicators() {
            const indicators = document.querySelectorAll(".indicator");
            indicators.forEach((indicator, index) => {
                if (index === currentIndex) {
                    indicator.classList.add("active");
                } else {
                    indicator.classList.remove("active");
                }
            });
        }

        function handleClick(direction) {
            // Based on the direction we call `scrollBy` with the item width we got earlier
            if(direction === "previous" && currentIndex > 0) {
                currentIndex -= 1;
                list.scrollBy({ left: -itemHeight / 2, behavior: "smooth" });
            } else if (direction === "previous" && currentIndex == 0) {
                currentIndex = items.length - 1;
                list.scrollBy({ left: (itemHeight) * (items.length), behavior: "smooth" });
            } else if(direction === "next" && currentIndex < items.length - 1) {
                currentIndex += 1;
                list.scrollBy({ left: itemHeight / 2, behavior: "smooth" });
            } else if(direction === "next" && currentIndex == items.length - 1) {
                currentIndex = 0;
                list.scrollBy({ left: (-itemHeight) * (items.length), behavior: "smooth" });
            }
            updateIndicators();
        }

        // Set initial current index based on scroll position after a delay to allow layout to load
        window.addEventListener("load", () => {
            setTimeout(setCurrentIndexFromScroll, 200); // Adjust timeout if necessary
        });
    </script>
<?php include 'footer.html'?>