   function uncheckOthers(radio) {
    var name = radio.name.substring(radio.name.lastIndexOf(':'));
    var elements = radio.form.elements;
    for (var i = 0; i < elements.length; i++) {
        if (elements[i].name.substring(elements[i].name.lastIndexOf(':')) === name) {
            elements[i].checked = false;
        }
    }
    radio.checked = true;
    
}

