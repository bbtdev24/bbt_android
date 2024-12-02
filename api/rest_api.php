<?php
$url	= "http://36.88.110.134:27/bbt_api/rest_server/api/login/?password";

$_POST['nik']	= urlencode($_POST['nik']);
$url 		.= "nik_baru=".$_POST['nik'];

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_HEADER, 0);
$output = curl_exec($ch);
curl_close($ch);

$result = json_decode($output);

}
?>