<?php
// $data = json_decode(file_get_contents('php://input'), TRUE);
// echo json_encode($data['username']);
// $username = $data['username'];
// $userpass = $data['userpass'];
// $data = @$_POST;
// echo json_encode(@$_POST['username']);
// echo json_encode($_POST);
// echo json_encode($_POST['userpass']);
// echo '新增成功!22';
$link = mysqli_connect("localhost", "admin", "mmcv1234", "strivelife");
$link -> set_charset("UTF8"); // 設定語系避免亂碼
$data=$_POST;
$data2=$_GET;

echo $_SERVER['REQUEST_METHOD'];

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    echo json_encode($data); 
    echo "33";
} else {
    echo json_encode($data2); 
    echo "33333";
}


// $result = $link -> query("SELECT * FROM `gamer`");
//     while ($row = $result->fetch_assoc()) // 當該指令執行有回傳
//     {
//         $output[] = $row; // 就逐項將回傳的東西放到陣列中
//     }

//     // 將資料陣列轉成 Json 並顯示在網頁上，並要求不把中文編成 UNICODE
//     print(json_encode($output, JSON_UNESCAPED_UNICODE));
//     $link -> close(); 

// if($username!=null && $userpass!=null){
//     echo $username;
//     echo $userpass;
// }
?>