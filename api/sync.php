<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, X-Secure-Token');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

require_once __DIR__ . '/config.php';

$input = file_get_contents('php://input');
$data = json_decode($input, true);
if (!$data) {
    $data = $_POST;
}

$paymentId = isset($data['paymentId']) ? $data['paymentId'] : '';
$uid = isset($data['uid']) ? $data['uid'] : '';
$email = isset($data['email']) ? $data['email'] : '';
$plan = isset($data['plan']) ? $data['plan'] : 'basic';
$amount = isset($data['amount']) ? intval($data['amount']) : 1000;
$ref = isset($data['reference']) ? $data['reference'] : '';
$link = isset($data['secureLink']) ? $data['secureLink'] : '';

// Optional database sync if MySQL credentials exist
$dbConnected = false;
if (defined('DB_HOST') && DB_USER !== 'YOUR_CPANEL_USER') {
    $conn = @new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
    if (!$conn->connect_error) {
        $dbConnected = true;
        $conn->query("CREATE TABLE IF NOT EXISTS vendoros_payments (
            id INT AUTO_INCREMENT PRIMARY KEY,
            payment_id VARCHAR(100) UNIQUE,
            uid VARCHAR(100),
            email VARCHAR(100),
            plan VARCHAR(50),
            amount INT,
            reference VARCHAR(150),
            secure_link TEXT,
            real_account VARCHAR(20) DEFAULT '" . REAL_OPAY_ACCOUNT . "',
            status VARCHAR(20) DEFAULT 'pending',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            INDEX (uid)
        )");

        $stmt = $conn->prepare("INSERT INTO vendoros_payments (payment_id, uid, email, plan, amount, reference, secure_link, real_account, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'pending') ON DUPLICATE KEY UPDATE status = VALUES(status)");
        if ($stmt) {
            $realAcc = REAL_OPAY_ACCOUNT;
            $stmt->bind_param("ssssisss", $paymentId, $uid, $email, $plan, $amount, $ref, $link, $realAcc);
            $stmt->execute();
            $stmt->close();
        }
        $conn->close();
    }
}

echo json_encode([
    'success' => true,
    'secure' => true,
    'message' => 'Synced securely to VendorOS SQL Gateway',
    'masked_account' => '7081****44',
    'reference' => $ref,
    'db_saved' => $dbConnected
]);
?>
