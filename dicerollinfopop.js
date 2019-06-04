var diceRollInfoPop = {
		id:"diceRollInfoPop",
		close:function(){
			var self = diceRollInfoPop;
			ir.get(self.id).classList.remove("show");
		},
		show:function(record,callback){
			var self = diceRollInfoPop;
			var pop = ir.get(self.id);
			if(pop.classList.contains("show"))
			{
				pop.classList.remove("show");
			}
			else
			{
				pop.classList.add("show");
			}
		},
		zz_diceRollInfoPop:0
}