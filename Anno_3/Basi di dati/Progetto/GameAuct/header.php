<!DOCTYPE html>
<html>
  <head>
    <title>GameAuct</title>
    <!-- <link rel="icon" type="image/x-icon" href="./img/Favicon.ico"> -->
    <link rel="stylesheet" href="./styles/styles_nav.css">
    <link rel="stylesheet" href="./styles/styles_body_standard.css">
    <link rel="stylesheet" href="./styles/style_footer.css">
    <link rel="stylesheet" href="./styles/styles_home.css">
    <link rel="stylesheet" href="./styles/styles_login.css">
    <link rel="stylesheet" href="./styles/styles_register.css">
    <link rel="stylesheet" href="./styles/styles_user.css">
    <link rel="stylesheet" href="./styles/styles_gallery.css">
    <link rel="stylesheet" href="./styles/styles_asta_page.css">
    <script src="https://kit.fontawesome.com/3f1192a822.js" crossorigin="anonymous"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
  </head>
    
  <body>
    <?php
      //Inizializza la sessione e prende tutti i dati di sessione precedentemente creati
      session_start();
      /**Inizializzazione variabile di stato gallery_status utilizzate nella pagina gallery.php
       * newest: avvia una query che ottiene le ultime 20 aste attive ordinate per data di inizio
       * user_aste: avvia una query che ottiene tutte le aste attive dell'utente (possibile miglioramento: visualizzare anche aste terminate)
       * gioco: avvia una query che ottiene tutte le aste attive della categoria videogioco
       * console: avvia una query che ottiene tutte le aste attive della categoria console
       * aste_old: avvia una query che ottiene tutte le aste terminate
       * user_puntate: avvia una query che ottiene tutte le puntate effettuate dall'utente (anche quelle su aste non più attive)
       * */
      $gallery_status = '';

      //Controlla se l'utente è già loggato
      //$is_logged_in = isset($_SESSION['user_id']);
      //Include script php per connessione al database
      include 'connection.php';

      //Inizializzazione PHP Data Object per nell'header così da poterlo utilizzare ovunque
      $pdo = get_db_connection();
      //Include script php per la gestione dell'utente
      include 'user_handling.php';

      //Imposta data e ora corrente (nell'header) nel formato del database così da poterli utilizzare ovunque
      date_default_timezone_set(date_default_timezone_get());
      $oggi = date('Y-m-d', time());
      $ora = date('H:i:s', time());
    ?>
    <nav class="topnav">

      <div class="mobile-logo">
        <a href="./index.php">
          <svg width="100" height="50" style="background-color:#eee;">
            <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#333">100x50</text>
          </svg>
        </a>
      </div>

      <label for="buttonNav" class="hamburg">
        <span class="fa-solid fa-bars-staggered"></span>
      </label>
      <input class="nav_input" type="checkbox" id="buttonNav">
      
      <ul class="topnav-list">

        <li> 
          <a class="logo" href="index.php">
            <svg width="100" height="50" style="background-color:#eee;">
              <text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="#333">100x50</text>
            </svg>
          </a>
        </li>
        <li> <a class="delayed1" href="grids.php?gallery_status=gioco">Videogiochi</a> </li>
        <li> <a class="delayed2" href="grids.php?gallery_status=console">Console</a> </li>
        <li> <a class="delayed2" href="grids.php?gallery_status=aste_old">Terminate</a> </li>
        <?php if (isset($_SESSION['user_id'])): ?>
          <li> <a class="delayed4" href="grids.php?gallery_status=user_aste">Le tue aste</a> </li>
        <?php endif; ?>
        <li>
        <?php if (isset($_SESSION['user_id'])): ?>
          <a class="delayed5" href="user.php"><i class="fa-solid fa-user-astronaut"></i></a>
        <?php else: ?>
          <a class="delayed5" href="login.php"><i class="fa-solid fa-user-secret"></i></a>
        <?php endif; ?>
        </li>

      </ul>

    </nav>

    <!--Semplice spazio per distanziare NavBar da HomePage-->
    <div style="padding-top: 15px"></div>