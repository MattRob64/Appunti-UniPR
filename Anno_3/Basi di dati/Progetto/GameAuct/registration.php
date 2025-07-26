<?php include 'header.php'?>
    <div class="register-container">
        <?php if (isset($_SESSION['user_id'])): ?>
            <p>You are already logged in. <a href="user.php">Go to user page</a></p>
        <?php else: ?>
            <h2>Registrazione</h2>
            
            <?php if (isset($_SESSION['error'])): ?>
                <p class="error"><?= $_SESSION['error'] ?></p>
                <?php unset($_SESSION['error']); ?>
            <?php endif; ?>
            
            <form method="POST">
                <input type="hidden" name="register" value="1">
                <p>
                    <label>Username:</label><br>
                    <input type="text" name="username" required>
                </p>
                <p>
                    <label>Email:</label><br>
                    <input type="email" name="email" required>
                </p>
                <p>
                    <label>Password:</label><br>
                    <input type="password" name="password" required>
                </p>
                <button type="submit">Registrati</button>
            </form>
            <p>Hai già un account? <a href="login.php">Accedi da qui</a></p>
        <?php endif; ?>
    </div>
<?php include 'footer.html'?>