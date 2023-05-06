document.addEventListener("DOMContentLoaded", function () {
    const yearFromSelect = document.getElementById("yearFrom");
    const yearToSelect = document.getElementById("yearTo");
    const currentYear = new Date().getFullYear();

    function addYearOption(select, year) {
        if (year < 2000 && year % 10 !== 0) {
            return;
        }

        const option = document.createElement("option");
        option.value = year;
        option.text = year;
        select.add(option);
    }

    for (let year = 1900; year <= currentYear; year++) {
        addYearOption(yearFromSelect, year);
        addYearOption(yearToSelect, year);
    }
});
