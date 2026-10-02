<?php
/**
 * VendorOS Secure Payment Page
 * User sees and transfers to real account ONLY HERE server-side.
 */
require_once __DIR__ . '/config.php';

$ref = isset($_GET['ref']) ? htmlspecialchars($_GET['ref']) : 'VOS-' . strtoupper(substr(md5(time()), 0, 8));
$amount = isset($_GET['amount']) ? htmlspecialchars($_GET['amount']) : '2000';
$plan = isset($_GET['plan']) ? htmlspecialchars($_GET['plan']) : 'pro';
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>VendorOS Secure Pay - Gateway</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; background: #0F172A; color: #F8FAFC; padding: 24px 16px; min-height: 100vh; display: flex; align-items: center; justify-content: center; }
        .card { background: #1E293B; border: 1px solid #334155; border-radius: 20px; padding: 28px 24px; max-width: 440px; width: 100%; box-shadow: 0 10px 25px rgba(0,0,0,0.5); }
        .badge { background: #10B981; color: #022C22; font-weight: 700; font-size: 11px; padding: 4px 10px; border-radius: 20px; display: inline-block; margin-bottom: 12px; text-transform: uppercase; letter-spacing: 0.5px; }
        h2 { font-size: 22px; font-weight: 700; margin-bottom: 6px; color: #FFFFFF; }
        .sub { font-size: 13px; color: #94A3B8; margin-bottom: 20px; }
        .detail-box { background: #0F172A; border-radius: 14px; padding: 16px; margin-bottom: 20px; border: 1px dashed #475569; }
        .row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; font-size: 14px; }
        .row:last-child { margin-bottom: 0; }
        .label { color: #94A3B8; }
        .val { font-weight: 600; color: #F1F5F9; }
        .acc-row { background: #1E3A8A; padding: 12px; border-radius: 10px; margin-top: 12px; display: flex; justify-content: space-between; align-items: center; }
        .acc-num { font-size: 20px; font-weight: 800; letter-spacing: 1px; color: #67E8F9; }
        .copy-btn { background: #38BDF8; color: #082F49; border: none; padding: 6px 14px; border-radius: 8px; font-weight: 700; font-size: 12px; cursor: pointer; }
        .btn-confirm { background: #25D366; color: white; padding: 16px; border-radius: 12px; width: 100%; border: none; font-weight: 700; font-size: 15px; cursor: pointer; text-decoration: none; display: block; text-align: center; margin-top: 16px; }
        .warning { color: #F59E0B; font-size: 12px; margin-top: 14px; line-height: 1.4; text-align: center; }
    </style>
</head>
<body>
<div class="card">
    <div class="badge">🔒 Verified Gateway</div>
    <h2>VendorOS Secure Payment</h2>
    <p class="sub">Reference: <strong style="color:#38BDF8"><?php echo $ref; ?></strong></p>

    <div class="detail-box">
        <div class="row">
            <span class="label">Plan:</span>
            <span class="val"><?php echo strtoupper($plan); ?></span>
        </div>
        <div class="row">
            <span class="label">Amount:</span>
            <span class="val" style="color:#34D399; font-size:17px; font-weight:700;">₦<?php echo number_format((int)$amount); ?></span>
        </div>
        <div class="row">
            <span class="label">Bank Name:</span>
            <span class="val"><?php echo REAL_OPAY_BANK; ?></span>
        </div>
        <div class="row">
            <span class="label">Account Name:</span>
            <span class="val"><?php echo REAL_OPAY_NAME; ?></span>
        </div>

        <div class="acc-row">
            <div>
                <div style="font-size:11px; color:#BAE6FD;">Transfer Account:</div>
                <div class="acc-num" id="accNumber"><?php echo REAL_OPAY_ACCOUNT; ?></div>
            </div>
            <button class="copy-btn" onclick="copyAcc()">COPY</button>
        </div>
    </div>

    <p style="font-size:13px; color:#CBD5E1;">Use <b><?php echo $ref; ?></b> as your transfer remark or narration.</p>

    <a class="btn-confirm" href="https://wa.me/2347081022844?text=I%20have%20transferred%20₦<?php echo $amount; ?>%20for%20VendorOS%20Ref:%20<?php echo $ref; ?>" target="_blank">
        I Don Pay - Confirm on WhatsApp
    </a>

    <p class="warning">
        🔒 This page is encrypted and managed server-side. Never send money to unverified accounts. Return to VendorOS app to click "I Don Pay - Verify Now".
    </p>
</div>

<script>
function copyAcc() {
    var text = document.getElementById("accNumber").innerText;
    navigator.clipboard.writeText(text).then(function() {
        alert("Account number copied: " + text);
    });
}
</script>
</body>
</html>
