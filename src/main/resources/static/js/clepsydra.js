function Clepsydra() {
    $('.timer').each(function () {
        const startDate = new Date($(this).data('start-date'));
        const endDate = new Date($(this).data('end-date'));
        const now = new Date();
        const elapsedTime = Math.max(now - startDate, 0);
        const remainingTime = Math.max(endDate - startDate - elapsedTime, 0);

        const seconds = Math.floor((remainingTime / 1000) % 60);
        const minutes = Math.floor((remainingTime / (1000 * 60)) % 60);
        const hours = Math.floor((remainingTime / (1000 * 60 * 60)) % 24);
        const days = Math.floor(remainingTime / (1000 * 60 * 60 * 24));

        $(this).text(`${days}d ${hours}h ${minutes}m ${seconds}s`);
    });
}

$(document).ready(function () {
    Clepsydra();
    setInterval(Clepsydra, 1000);
});
