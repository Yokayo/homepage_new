$(main);

function main(){ // инициализация
    var params = new URLSearchParams(window.location.search);
    if(params.has('userid')){ // id пользователя, чью страничку смотрим (в нашем случае всегда один и тот же)
        currentID = parseInt(params.get('userid'));
        if(isNaN(currentID))
            currentID = 3;
    }
    else
        currentID = 3;
    currentSection = '';
    isEditMode = false;
    main_section = $(".main_section");
    var d = new XMLHttpRequest();
    d.open('GET', '/getPersonName?id=' + currentID); // загрузка имени
    d.send();
    d.onreadystatechange = function(){
        if(!checkReady(this))
            return;
        document.title = JSON.parse(this.responseText).name;
    }
    loadModule('base'); // зарузка модуля "кратко обо мне"
    $(".bg_link").click(function(){ // коллбэки для кнопок
        loadModule('bg');
    });
    $(".base_link").click(function(){
        loadModule('base');
    });
    $(".skills_link").click(function(){
        loadModule('skills');
    });
    $(".edit_button").click(function(){ // редактирование
        if(isEditMode){
            $(this).html("Редактировать секцию");
            $(".save_button").hide();
            isEditMode = false;
            main_section.empty().children().unbind();
            main_section.html(main_section_backup);
            return;
        }
        main_section_backup = main_section.html();
        $(this).html("Выйти без сохранения");
        $(".save_button").css({display: 'inline-block'});
        isEditMode = true;
        switch(currentSection){ // интерфейс редактирования
            case 'base':
                main_section.html('<textarea class="edit_base">' + $(".main_text").html() + '</textarea>');
                break;
            case 'bg':
                main_section.html('<textarea class="edit_bg">' + main_section.html() + '</textarea>');
                break;
            case 'skills':
                $(".skill").addClass('skill_editable').click(function(){
                    $(this).hide();
                    $(this).after('<input type="text" id="' + $(this).prop('id') + '" size="' + $(this).html().length + '"></input>');
                    $("input").filter("#" + $(this).prop('id')).val($(this).html()).keyup(function(event){ // применение изменений навыка
                        if(event.keyCode === 13){
                            if($(this).val() == ''){
                                $(".skill_editable").filter("#" + $(this).prop('id')).remove();
                                var comma = $(".comma").filter("#" + $(this).prop('id'));
                                if(comma.length == 0){
                                    $(this).prev().remove();
                                }else
                                    comma.remove();
                                $(this).remove();
                            }else{
                                $(".skill_editable").filter("#" + $(this).prop('id')).show().html($(this).val());
                                $(this).remove();
                            }
                        }
                    });
                });
        }
    });
    $(".save_button").click(function(){
        d = new XMLHttpRequest();
        var formData = new FormData();
        formData.append('id', currentID.toString());
        $(".edit_button").html("Редактировать секцию");
        switch(currentSection){
            case 'base':
                var newBase = $(".edit_base").val();
                formData.append('newBrief', newBase);
                alert('newBrief = ' + $(".edit_base").val());
                d.open('POST', '/setPersonBrief');
                d.send(formData);
                d.onreadystatechange = function(){
                    if(!checkReady(this))
                        return;
                    alert(JSON.parse(this.responseText).result);
                    isEditMode = false;
                }
                $(".edit_base").remove();
                main_section.html(newBase);
                break;
            case 'bg':
                var newBG = $(".edit_bg").val();
                formData.append('newBG', newBG);
                d.open('POST', '/setPersonBG');
                d.send(formData);
                d.onreadystatechange = function(){
                    if(!checkReady(this))
                        return;
                    alert(JSON.parse(this.responseText).result);
                    isEditMode = false;
                }
                $(".edit_bg").remove();
                main_section.html(newBG);
                break;
            case 'skills':
                $("input").trigger(jQuery.Event('keyup', {keyCode: 13}));
                var skills = $(".skill");
                for(var a = 0; a < skills.length; a++){
                    formData.append('skill', $(skills.get(a)).attr('type') + '_' + $(skills.get(a)).html());
                }
                d.open('POST', '/setPersonSkills');
                d.send(formData);
                d.onreadystatechange = function(){
                    if(!checkReady(this))
                        return;
                    alert(JSON.parse(this.responseText).result);
                    isEditMode = false;
                }
                skills.removeClass('skill_editable');
                $("input").remove();
                break;
        }
        $(this).hide();
    });
}
    
function loadModule(name){ // функция загрузки модулей
    if(currentSection == name){
        return;
    }
    var d = new XMLHttpRequest();
    $(".main_section").fadeOut(50, function(){
        switch(name){
            case 'base':
                d.open('GET', '/getPersonBrief?id=' + currentID);
                d.send();
                d.onreadystatechange = function(){
                    if(!checkReady(this)) // готов ли ответ
                        return;
                    currentSection = 'base'; // чтобы не загружать два раза подряд одно и то же
                    var response = JSON.parse(this.responseText);
                    $(".main_section").fadeIn(250, function(){}).css({display: 'inline-block'})
                    .html('<span class="main_text"></span>');
                    $(".main_text").html(response.brief);
                }
                break;
            case 'bg':
                currentSection = 'bg';
                d.open('GET', '/getPersonBG?id=' + currentID);
                d.send();
                d.onreadystatechange = function(){
                    if(!checkReady(this))
                        return;
                    var response = JSON.parse(this.responseText);
                    $(".main_section").html(response.BG).fadeIn(250, function(){}).css({display: 'inline-block'});
                }
                break;
            case 'skills':
                currentSection = 'skills';
                d.open('GET', '/getPersonSkills?id=' + currentID);
                d.send();
                d.onreadystatechange = function(){
                    if(!checkReady(this))
                        return;
                    var response = JSON.parse(this.responseText);
                    var text = '';
                    var arrays = []; // всё делаем перебором
                    for(var a = 0; a < 3; a++) // таким образом сохраняется возможность добавить новую категорию навыков
                        arrays.push(new Array());
                    for(var a = 0; a < response.Skills.length; a++){ // идём наполнять массивы категорий навыков
                        switch(response.Skills[a].type){
                            case 'back':
                                if(arrays[0].length == 0){
                                    arrays[0].push('<b>Бэк</b>: ');
                                }
                                arrays[0].push(skillDefinition(response.Skills[a].name, response.Skills[a].type, a));
                                arrays[0].push(commaDefinition(a));
                                break;
                            case 'front':
                                if(arrays[1].length == 0){
                                    arrays[1].push('<b>Фронт</b>: ');
                                }
                                arrays[1].push(skillDefinition(response.Skills[a].name, response.Skills[a].type, a));
                                arrays[1].push(commaDefinition(a));
                                break;
                            default:
                                if(arrays[2] == 0){
                                    arrays[2].push('<b>Другое</b>: ');
                                }
                                arrays[2].push(skillDefinition(response.Skills[a].name, response.Skills[a].type, a));
                                arrays[2].push(commaDefinition(a));
                        }
                    }
                    for(var a = 0; a < arrays.length; a++){ // теперь формируем текст на основе массивов
                        if(arrays[a].length == 0)
                            continue;
                        arrays[a].splice(arrays[a].length-1, 1);
                        for(var b = 0; b < arrays[a].length; b++){
                            text += arrays[a][b];
                        }
                        if(a != arrays.length - 1)
                            text += '<br/>';
                    }
                    $(".main_section").html(text).fadeIn(250, function(){}).css({display: 'inline-block'});
                }
                break;
        }
    });
}

function skillDefinition(skill, type, id){
    return '<span class="skill" type="' + type + '" id="' + id + '">' + skill + '</span>';
}
function commaDefinition(id){
    return '<span class="comma" id="' + id + '">, </span>';
}

function checkReady(response){ // проверка готовности ответа и обработка ошибок
        if(response.readyState != 4)
            return false;
        if(response.status == 404){
            alert('Пользователь с таким ID не найден');
            return false;
        }
        if(response.status == 400){
            alert('Ошибка запроса (возможно, отсутствует один из параметров)');
            return false;
        }
        return true;
}