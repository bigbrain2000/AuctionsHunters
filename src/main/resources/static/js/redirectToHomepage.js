function countdown() {
    var seconds = document.getElementById("countdown").innerHTML;
    seconds = parseInt(seconds, 10);

    if (seconds === 0) {
        clearInterval(countdownTimer);
        window.location = "/";
    }

    document.getElementById("countdown").innerHTML = seconds - 1;
}

var countdownTimer = setInterval(countdown, 1000);