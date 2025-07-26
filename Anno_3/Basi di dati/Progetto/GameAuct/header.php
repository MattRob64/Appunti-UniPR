<!DOCTYPE html>
<html>
  <head>
    <title>GameAuct</title>
    <link rel="icon" type="image/x-icon" href="./img/Favicon.ico"> 
    <link rel="stylesheet" href="./styles/styles_nav.css">
    <link rel="stylesheet" href="./styles/styles_body_standard.css">
    <link rel="stylesheet" href="./styles/style_footer.css">
    <link rel="stylesheet" href="./styles/styles_home.css">
    <link rel="stylesheet" href="./styles/styles_login.css">
    <link rel="stylesheet" href="./styles/styles_register.css">
    <script src="https://kit.fontawesome.com/3f1192a822.js" crossorigin="anonymous"></script>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
  </head>
    
  <body>
    <?php
      //Inizializza la sessione e prende tutti i dati di sessione precedentemente creati
      session_start();
      //Controlla se l'utente è già loggato
      $is_logged_in = isset($_SESSION['user_id']);
      include 'connection.php';

      $pdo = get_db_connection();
      include 'user_handling.php';

      // Fetch general data (replace with your actual query)
      /*$stmt = $pdo->query("
      SELECT item_id, item_name, description, created_at 
      FROM public_data 
      ORDER BY created_at DESC 
      LIMIT 5
      ");
      $public_items = $stmt->fetchAll(PDO::FETCH_ASSOC);*/

      /*$result = pg_query($connection, "SELECT idutente, nomeutente, email, biografia, avatar FROM public.utente;");
      if(!$result) {
          echo "An Error Has Occured <br>";
          exit;
      }*/
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
        <li> <a class="delayed1" href="index.php">Videogiochi</a> </li>
        <li> <a class="delayed2" href="index.php">Console</a> </li>
        <!-- <li> <a class="delayed3" href="index.php">Ultimi Annunci</a> </li> --> 
        <?php if ($is_logged_in): ?>
          <li> <a class="delayed5" href="#">Le tue aste</a> </li>
        <?php endif; ?>
        <li>
        <?php if ($is_logged_in): ?>
          <a class="delayed4" href="user.php"><i class="fa-solid fa-user-astronaut"></i></a>
        <?php else: ?>
          <a class="delayed4" href="login.php"><i class="fa-solid fa-user-secret"></i></a>
        <?php endif; ?>
        </li>
        <!-- <li> <a class="delayed5" href="#">Varie</a> </li>
        <li> <a class="delayed6" href="#">Contatti</a> </li> -->

      </ul>

    </nav>

    <!--Semplice spazio per distanziare NavBar da HomePage-->
    <div style="padding-top: 15px"></div>