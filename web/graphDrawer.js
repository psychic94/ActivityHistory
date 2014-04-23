function drawGraph(hourly){
	var canvas = document.getElementById("graph");
	var context = canvas.getContext("2d");
	var width = canvas.width;
	var height = canvas.height;
	
	context.font = "10px Arial";
	
	//y axis labels
	context.fillText("100", 2, 12);
	context.fillText("90", 9, (height-36)/10+12);
	context.fillText("80", 9, 2*(height-36)/10+12);
	context.fillText("70", 9, 3*(height-36)/10+12);
	context.fillText("60", 9, 4*(height-36)/10+12);
	context.fillText("50", 9, 5*(height-36)/10+12);
	context.fillText("40", 9, 6*(height-36)/10+12);
	context.fillText("30", 9, 7*(height-36)/10+12);
	context.fillText("20", 9, 8*(height-36)/10+12);
	context.fillText("10", 9, 9*(height-36)/10+12);
	context.fillText("0", 16, 10*(height-36)/10+12);
	
	//x axis labels
	context.rotate(-Math.PI/4);
	for(var i=0; i<25; i++){
		var x = i*(width-27)/25+25;
		var y = height-2;
		var hypot = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
		var angle = Math.PI/4-Math.atan(x/y);
		if(i<10){
			context.fillText("0"+i+":00", -hypot*Math.sin(angle)+7, hypot*Math.cos(angle)+7);
			//~context.fillText("0"+i+":00", i*(width-27)/25-width*.24, height*.8+i*height*.095);
		}else if(i<24){
			context.fillText(i+":00", -hypot*Math.sin(angle)+7, hypot*Math.cos(angle)+7);
		}else{
			context.fillText("00:00", -hypot*Math.sin(angle)+7, hypot*Math.cos(angle)+7);
		}
	}
	context.rotate(Math.PI/4);
	
	//axes
	context.beginPath();
	context.moveTo(25, 2);
	context.lineTo(25, height-32);
	context.lineTo(width-2, height-32);
	context.stroke();
	
	//Draw data
	context.beginPath();
	context.moveTo(35, hourly[0]*(height-36)+12);
	for(var hour=1; hour<24; hour++){
		context.lineTo(hourly[hour]*(width-27)/25+35, hourly[hour]*(height-36)+12);
	}
	context.lineTo(24*(width-27)/25+35, hourly[0]*(height-36)+12);
}