<?php
$url1=$_SERVER['REQUEST_URI'];
    header("Refresh: 5; URL=$url1");
	if(isset($_GET['Temp']))
	{
		$Battery = $_GET['Bat'].'@';
		$Charging = $_GET['Status'].'@';
		$Temperature = $_GET['Temp'].'@';
		$Voltage = $_GET['Volt'].'@'.date("h:i:s")." ".date("d/m/Y")."@";
				
		$myfile = fopen("data.txt", "a") or die("Unable to open file!");
		fwrite($myfile, $Battery);
		fwrite($myfile, $Charging);
		fwrite($myfile, $Temperature);
		fwrite($myfile, $Voltage);
		$myfile1 = fopen("Status.txt", "w") or die("Unable to open file!");
		fwrite($myfile1, $Battery);
		fwrite($myfile1, $Charging);
		fwrite($myfile1, $Temperature);
		fwrite($myfile1, $Voltage);
		fclose($myfile1);
	}
	else
	{
		if(isset($_GET['log'])){
			$myfile = fopen("data.txt", "r") or die("Unable to open file!");
			$data = file_get_contents("data.txt");
			$data_str = explode("@",$data);
		}
		else{
			$myfile = fopen("Status.txt", "r") or die("Unable to open file!");
			$data = file_get_contents('Status.txt');
			$data_str = explode("@",$data);
		}
		echo '<!DOCTYPE html><html><head><title> IOT base Battery Monitoring System </title><link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
<script src="//netdna.bootstrapcdn.com/bootstrap/3.0.0/js/bootstrap.min.js"></script>
<script src="//code.jquery.com/jquery-1.11.1.min.js"></script>
</head><body><center><h1>IOT base Battery Monitoring System</h1></center><div class=""col-md-2></div><div class="col-md-8">
				<table class="table table-striped">
				  <tr>
					<th>Battery %</th>
					<th>Charging_Status </th>
					<th>Battery_Temperature</th>
					<th>Battery Voltage</th>					
					<th>Date_Time</th>';
		
		$i=0;
		foreach($data_str as $data_string){
			if(!($i%5))
			{
				echo '</tr><tr>';
//				$i=0;
			}
			echo '<th>'.$data_string.'</th>';
			$i++;
		  }
	  echo '</tr>';
		echo '</table></div></body></html>';
	}
?>
