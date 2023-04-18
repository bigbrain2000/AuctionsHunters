const imagesInput = document.getElementById('images');
const thumbnails = document.querySelector('.thumbnails');

imagesInput.addEventListener('change', (e) => {
    const files = e.target.files;
    for (let file of files) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const img = document.createElement('img');
            img.src = e.target.result;
            thumbnails.appendChild(img);
        };
        reader.readAsDataURL(file);
    }
});
