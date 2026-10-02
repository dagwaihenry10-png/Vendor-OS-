<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

require_once __DIR__ . '/config.php';

$uid = isset($_GET['uid']) ? $_GET['uid'] : '';

$rows = [];
if (defined('DB_HOST') && DB_USER !== 'YOUR_CPANEL_USER') {
    $conn = @new mysqli(DB_HOST, DB_USER, DB_PASS, DB_NAME);
    if (!$conn->connect_error) {
        $safeUid = $conn->real_escape_string($uid);
        $result = $conn->query("SELECT payment_id, uid, plan, amount, reference, status, created_at FROM vendoros_payments WHERE uid = '$safeUid' ORDER BY created_at DESC LIMIT 50");
        if ($result) {
            while ($r = $result->fetch_assoc()) {
                $rows[] = $r;
            }
        }
        $conn->close();
    }
}

echo json_encode([
    'success' => true,
    'masked_account' => '7081****44',
    'count' => count($rows),
    'payments' => $rows
]);
?>
