<!DOCTYPE html>
<html>
	<head>
		<script type="text/javascript" src="graphDrawer.js"> </script>
	</head>
	<body><form>
		Username: <input type="text" maxlength="15" name="player" required /><br />
		<table>
			<tr><td>Start:</td><td><input type="text" name="start" placeholder="YYYY-MM-DD hh:mm:ss" maxlength="19" size="25" /></td></tr>
			<tr><td>End:</td><td><input type="text" name="end" placeholder="YYYY-MM-DD hh:mm:ss" maxlength="19" size="25" /></td></tr>
		</table>
		<input type="submit" value="Query" />
	</form></br>
	<?php
		//Import configuration
		include 'config.php';
		
		$link = mysql_connect($address, $username, $password);
		mysql_select_db($database, $link);
		
		//Select the specified player's sessions, then make sure there's at least 1 session
		$query = "SELECT * FROM Players WHERE PlayerName = $_GET[player] AS Sessions";
		mysql_query($query);
		$query = "SELECT Count(EntryID) FROM Sessions";
		$temp = mysql_query($query);
		if($temp<1){
			die("Error: There is no record of that player.");
		}
		
		//Turn URL variables into regular variables with idiot proofing
		if($_GET[start]==""){
			//Default to the first recorded session
			$query = "SELECT Min(Start) FROM (SELECT Start FROM Players WHERE PlayerName = $_GET[player])";
			$startDate = mysql_query($query);
		}else{
			$startDate = strtotime($_GET[start]);
			if(!$startDate){
				die("Error: The start date is invalid.");
			}
		}
		if($_GET[end]==""){
			//Default to the the current time
			$endDate = time();
		}else{
			$endDate = strtotime($_GET[end]);
			if(!$endDate){
				die("Error: The end date is invalid.");
			}
		}
		if($startDate>$endDate){
			die("Error: The end date is after the start date.");
		}
		
		//Accounts for different ways the ranges can overlap by
		//making a subtotal for each case and then adding them together
		
		//Date order: $startDate, Start, End, $endDate
		//Dates to compare: Start, End
		$query = "SELECT * FROM Sessions WHERE Start > $startDate AND End < $endDate AS CaseA";
        mysql_query($query);
		$query = "SELECT Sum(DATEDIFF(Start, End)) FROM CaseA";
		$total = mysql_query($query);
		for($hour=0; $hour<24; $hour++){
			$query = "SELECT Sum(DATEDIFF(Start, End)) FROM (SELECT * FROM CaseA WHERE DATEPART(hh,Start) = $hour)";
			$hourly[$hour] = mysql_query($query);
		}
		//Date order: Start, $startDate, $endDate, End
		//Dates to compare: $startDate, $endDate
		$query = "SELECT * FROM Sessions WHERE Start < $startDate AND End > $endDate AS CaseB";
        mysql_query($query);
		$query = "SELECT Count(EntryID) FROM CaseB";
		$temp = mysql_query($query);
		$total += $temp*($endDate-$startDate);
		for($hour=0; $hour<24; $hour++){
			$query = "SELECT Count(EntryID) FROM (SELECT * FROM CaseB WHERE DATEPART(hh,Start) = $hour)";
			$temp = mysql_query($query);
			$hourly[$hour] += $temp*($endDate-$startDate);
		}
		//Date order: $startDate, Start, $endDate, End
		//Dates to compare: Start, $endDate
		$query = "SELECT * FROM Sessions WHERE Start BETWEEN $startDate AND $endDate AND $endDate BETWEEN Start AND End AS CaseC";
		mysql_query($query);
		$query = "SELECT Sum(DATEDIFF(Start, End)) FROM CaseA";
		$total += mysql_query($query);
		for($hour=0; $hour<24; $hour++){
			$query = "SELECT Sum(DATEDIFF(Start, $endDate)) FROM (SELECT * FROM CaseC WHERE DATEPART(hh,Start) = $hour)";
			$hourly[$hour] += mysql_query($query);
		}
		//Date order: Start, $startDate, End, $endDate
		//Dates to compare: $startDate, End
		$query = "SELECT * FROM Sessions WHERE $startDate BETWEEN Start AND End AND End BETWEEN $startDate AND $endDate AS CaseD";
		mysql_query($query);
		$query = "SELECT Sum(DATEDIFF(Start, End)) FROM CaseA";
		$total += mysql_query($query);
		for($hour=0; $hour<24; $hour++){
			$query = "SELECT Sum(DATEDIFF($startDate, End)) FROM (SELECT * FROM CaseD WHERE DATEPART(hh,Start) = $hour)";
			$hourly[$hour] += mysql_query($query);
		}
		
		for($hour=0; $hour<24; $hour++){
			$hourly /= $endDate - $startDate;
		}
		
		//Create and print result string
		$temp = "Total: ".$total." hours(".(100.0*$total)/($endDate-$startDate)."%)<br />";
		echo $temp;
		//Create and draw result graph
		drawGraph($hourly);
	?>
	<canvas id="graph" height="400" width="1000">No graph yet</canvas>
</body></html>