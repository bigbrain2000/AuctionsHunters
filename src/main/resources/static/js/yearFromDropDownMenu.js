document.addEventListener("DOMContentLoaded", function () {
    const yearFromSelect = document.getElementById("yearFrom");
    const currentYear = new Date().getFullYear();

    for (let year = 1900; year <= currentYear; year++) {
        if (year < 2000 && year % 10 !== 0) {
            continue;
        }

        const option = document.createElement("option");
        option.value = year;
        option.text = year;
        yearFromSelect.add(option);
    }
});
