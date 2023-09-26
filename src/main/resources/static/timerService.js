var workTimer = createTimer(document.getElementById("workTimer"));
var breakTimer = createTimer(document.getElementById("breakTimer"));
var workInProgress = false;
var breakInProgress = false;
var isBreak = false;

document.addEventListener("DOMContentLoaded", function() {
    document.getElementById('notificationBanner').style.display = 'none';
});

document.getElementById("toggleWorkBtn").addEventListener("click", function() {
    if (workInProgress) {
        stopTimer(workTimer);
        stopTimer(breakTimer);
        document.getElementById("workTimer").textContent = "00:00:00";
        document.getElementById("breakTimer").textContent = "00:00:00";
        sendTimeUpdate("endWork");
        this.textContent = "Start Arbeit";
        document.getElementById("toggleBreakBtn").disabled = true;
        document.getElementById("toggleBreakBtn").title = "Sie haben Feierabend! Dort sind keine Pausen möglich.";
        document.getElementById('notificationBanner').style.display = 'none';
    } else {
        startTimer(workTimer);
        sendTimeUpdate("startWork");
        this.textContent = "Feierabend!";
        document.getElementById("toggleBreakBtn").disabled = false;
        document.getElementById("toggleBreakBtn").title = "";
    }
    workInProgress = !workInProgress;
});


document.getElementById("toggleBreakBtn").addEventListener("click", function() {
    if (breakInProgress) {
        workTimer.resetBannerTime();
        pauseTimer(breakTimer);
        startTimer(workTimer);
        sendTimeUpdate("endBreak");
        this.textContent = "Pause!";
        document.getElementById("toggleWorkBtn").disabled = false;
        document.getElementById("toggleWorkBtnWrapper").title = "";
        isBreak = false;
    } else {
        pauseTimer(workTimer);
        startTimer(breakTimer);
        sendTimeUpdate("startBreak");
        this.textContent = "Pause beenden";
        document.getElementById("toggleWorkBtn").disabled = true;
        document.getElementById("toggleWorkBtnWrapper").title = "Feierabend innerhalb einer Pause ist nicht möglich.";
        document.getElementById('notificationBanner').style.display = 'none';
        isBreak = true;
    }
    breakInProgress = !breakInProgress;
});

function createTimer(element) {
    var time = 0;
    var bannerTime = 0;  // Neue Variable, um die Zeit für das Banner zu verfolgen

    var interval;
    return {
        start: function() {
            if (!interval) {
                interval = setInterval(function() {
                    time++;
                    bannerTime++;  // Erhöhe die bannerTime

                    // Konvertieren der Zeit in Stunden, Minuten und Sekunden und Aktualisieren des Elements
                    var hours = Math.floor(time / 3600);
                    var minutes = Math.floor((time - (hours * 3600)) / 60);
                    var seconds = time - (hours * 3600) - (minutes * 60);
                    element.textContent = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

                    // Wenn 10 Sekunden vergangen sind und keine Pause im Gange ist, wird der Banner angezeigt.
                    if (bannerTime % 10 === 0 && !isBreak) {
                        showBanner();
                    }
                }, 1000);
            }
        },
        pause: function() {
            clearInterval(interval);
            interval = null;
        },
        stop: function() {
            clearInterval(interval);
            time = 0;
            bannerTime = 0;  // bannerTime zurücksetzen, wenn der Timer gestoppt wird
            interval = null;
        },
        resetBannerTime: function() { // Methode um bannerTime zurückzusetzen
            bannerTime = 0;
        }
    };
}
function startTimer(timer) {
    timer.start();
}

function pauseTimer(timer) {
    timer.pause();
}

function stopTimer(timer) {
    timer.stop();
}

function showBanner() {
    document.getElementById('notificationBanner').style.display = 'block';
}

function sendTimeUpdate(action) {
    fetch("/saveArbeitszeit", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `action=${action}`
    });
}
