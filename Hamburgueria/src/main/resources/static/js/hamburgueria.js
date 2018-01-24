$(document).ready(function() {
    $('select').material_select();
});

$(document).ready(function() {
	$(".dropdown-button").dropdown();
});

$(document).ready(function(telefone) {
	$(".dropdown-button").mascara(telefone);
});

$('.datepicker').pickadate({
	  selectMonths: true,
	  selectYears: 80,
	  // Título dos botões de navegação
	  labelMonthNext: 'Próximo Mês',
	  labelMonthPrev: 'Mês Anterior',
	  // Título dos seletores de mês e ano
	  labelMonthSelect: 'Selecione o Mês',
	  labelYearSelect: 'Selecione o Ano',
	  // Meses e dias da semana
	  monthsFull: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
	  monthsShort: ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'],
	  weekdaysFull: ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'],
	  weekdaysShort: ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sab'],
	  // Letras da semana
	  weekdaysLetter: ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'],
	  //Botões
	  today: 'Hoje',
	  clear: 'Limpar',
	  close: 'Ok',
	  // Formato da data que aparece no input
	  format: 'dd/mm/yyyy',
	  onClose: function() {
	    $(document.activeElement).blur()
	  }
	});


function mascara(telefone){ 
    if(telefone.value.length == 0)
        telefone.value = '(' + telefone.value; //quando começamos a digitar, o script irá inserir um parênteses no começo do campo.
    if(telefone.value.length == 3)
        telefone.value = telefone.value + ') '; //quando o campo já tiver 3 caracteres (um parênteses e 2 números) o script irá inserir mais um parênteses, fechando assim o código de área.

    if(telefone.value.length == 10)
        telefone.value = telefone.value + '-'; //quando o campo já tiver 10 caracteres, o script irá inserir um tracinho, para melhor visualização do telefone.
}
