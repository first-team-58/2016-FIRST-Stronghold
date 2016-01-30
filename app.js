var db = new PouchDB('scouting');

function save(formData) {
    db.put(formData);
}
        
function cleanData(dataset) {
    $(document).ready(function(){
        $("input").each(function){
            dataset += $(this).name()+:+$(this).value()+,;  
        }
        $("select").each(function){
            dataset += $(this).name()+:+$(this).value()+,;  
        }
    })
    return dataset;
}

function saveMatchForm() {
    var formData = cleanData();
    save(formData);
}
