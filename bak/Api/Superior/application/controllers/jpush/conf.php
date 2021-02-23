<?php
require __DIR__ . '/autoload.php';

use JPush\Client as JPush;

$app_key = 'fba33a9922ac91da73db53c5';
$master_secret = '167250cda8d95bb611e06f24';
$registration_id = getenv('registration_id');

$client = new JPush($app_key, $master_secret);
