const passwordInput = document.getElementById("password");
const togglePassword = document.getElementById("togglePassword");

togglePassword.addEventListener("change", function () {
    if (togglePassword.checked) {
        passwordInput.setAttribute("type", "text");
    } else {
        passwordInput.setAttribute("type", "password");
    }
});
