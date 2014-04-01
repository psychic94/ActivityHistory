<!DOCTYPE html>
<html><body>
	<form>
		Username: <input type="text" maxlength="15" name="player" required /><br />
		<table>
			<tr><td>Start:</td><td><input type="text" name="start" placeholder="YYYY-MM-DD hh:mm:ss" maxlength="19" size="25" /></td></tr>
			<tr><td>End:</td><td><input type="text" name="end" placeholder="YYYY-MM-DD hh:mm:ss" maxlength="19" size="25" /></td></tr>
		</table>
		<input type="submit" value="Query" />
	</form>
	<?php
		$startDate = strtotime($_GET[start]);
		$endDate = strtotime($_GET[end]);
		#####Fill in database information here#####
        $link = mysql_connect('address', 'username', 'password');
		mysql_select_db('database', $link);
		//TODO: Make different cases for range overlap by altering this line,
		//making a subtotal, repeating, and summing
		$query = "SELECT * FROM Players WHERE PlayerName = $_GET[player] AND (Start BETWEEN $startDate AND $endDate OR End BETWEEN $startDate AND $endDate) AS Sessions";
        mysql_query($query);
		for($hour=0; $hour<24; $hour++){
			//The possibly redundant embedded query is a fail safe
			$query = "SELECT SUM(DATEDIFF(Start, End)) FROM (SELECT * FROM Sessions WHERE DATEPART(hh,Start) = $hour)";
			$data[$hour] = mysql_query($query);
		}
	?>
</body></html>