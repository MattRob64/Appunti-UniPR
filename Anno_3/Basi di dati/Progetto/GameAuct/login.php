<?php include 'header.php'?>
    <div class="login-container">
        <?php if (isset($_SESSION['user_id'])): ?>
            <p>You are already logged in. <a href="user.php">Go to user page</a></p>
        <?php else: ?>
            <h2>Accesso</h2>
            
            <?php if (isset($_SESSION['error'])): ?>
                <p class="error"><?= $_SESSION['error'] ?></p>
                <?php unset($_SESSION['error']); ?>
            <?php endif; ?>
            
            <?php if (isset($_SESSION['success'])): ?>
                <p class="success"><?= $_SESSION['success'] ?></p>
                <?php unset($_SESSION['success']); ?>
            <?php endif; ?>
            
            <form method="POST">
                <input type="hidden" name="login" value="1">
                <p>
                    <label>Email:</label><br>
                    <input type="email" name="email" required>
                </p>
                <p>
                    <label>Password:</label><br>
                    <input type="password" name="password" required>
                </p>
                <button type="submit">Accedi</button>
            </form>
            <p>Non hai un account? <a href="registration.php">Registrati qui</a></p>
        <?php endif; ?>
    </div>
<?php include 'footer.html'?>