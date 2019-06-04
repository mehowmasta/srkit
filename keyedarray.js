"use strict";
/** 
 Usage: var keyedArray = new KeyedArray("keyAttribute")
<br>or: var keyedArray = new KeyedArray(function(r){return r.a + r.b;})
<br>or: var keyedArray = new KeyedArray("keyAttribute",[{r:1},{r:2},{r:3}...]);
<br> best if keys are unique and don't change, but add([]) re-sorts
*/ 
var KeyedArray = (function(indexOn)
{       
   var Constructor = function(indexOn, optArray,wrapperFunc) {
      this.values = [];
      this.keyFunction = indexOn;
      this.wrapperFunc = wrapperFunc;
      if (typeof(indexOn) == "string")
      {// it's an attribute name, wrap it in a function
         this.keyFunction = function(o){return o==null ? "" : o[indexOn] || "";};
      }
      if (optArray != null)
      {
         this.add(optArray);
      }
   };
   Constructor.prototype = {
      keyIndexMap:{},
      constructor : KeyedArray,
      /** adds an entry or an array of entries */
      add : function(newEntry,wrapperFunc) {
         if (newEntry == undefined) {
            return;
         }
         if (wrapperFunc != null) {
        	 this.wrapperFunc=wrapperFunc;
         }
         if(newEntry.splice) {
    		for (var i=0,z=newEntry.length;i<z;i++) {    			
    			this.set(newEntry[i]);
    		}
         } else {
        	 this.set(newEntry);
         }
      },
      /** clears elements */
      clear : function() {
         this.keyIndexMap = {};
         this.values = [];
      },
      /** fill this.keyIndexMap from the passed index onward */
      fillMap:function(fromIndex)
      {
         var a = this.values;
         var fn = this.keyFunction;
         var m = this.keyIndexMap;
         for (var i=fromIndex,z=a.length;i<z;i++)
         {
            m[fn(a[i])] = i;
         }
      },
      /**
       * returns an array of elements where filterFunction(element)==true
       * <br>if filterFunction is null, returns all
       * <br>sorts result by comparator
       * <br>if comparator is null, sequence will be arrival
       */
      filter : function(filterFunction, comparator)
      {        
         var result = [];
         if (filterFunction == null) {
            result = this.values.slice(0);
         } else {
            var a = this.values;
            for (var i=0,z=a.length;i<z;i++) {
               var element = a[i];
               if (filterFunction(element)) {
                  result.push(element);
               }
            }
         }
         if (comparator != null) {
            result.sort(comparator);
         }
         return result;
      },
      /**
       * returns boolean, whether any element has been found that evaluates
       * checkFunction(e) to true
       */
      findOne : function(checkFunction) {        
        var a = this.values;        
        for (var i=0,z=a.length;i<z;i++) {
           if (checkFunction(a[i])) {
        	   return true;
           }
        }
        return false;
      },
      /**
       * Runs the passed function on each element.
       * @param void function(element)
       */
      forEach : function(runFunction) {        
        var a = this.values;        
        for (var i=0,z=a.length;i<z;i++) {
        	runFunction(a[i]);
        }
      },
      /** return element with passed key, or null if not found */
      get : function(k)
      {
         var idx = this.indexOf(k);
         return idx < 0 ? null : this.values[idx];
      },
      /** returns element at the passed index */
      getAt : function(index)
      {
         return this.values[index];
      },
      /** return the index at which the key is found, or -1 */
      indexOf:function(searchKey) {
         searchKey = searchKey+"";
         var a = this.values;
         var fn = this.keyFunction;
         var m = this.keyIndexMap;
         var idx = m[searchKey];
         if (idx != null) {
            var o = a[idx];
            if (fn(o) == searchKey) {
               return idx;
            }
         }
         for (var i=0,z=a.length;i<z;i++) {
            var o = a[i];
            var k = fn(o);
            m[k] = i;
            if (k == searchKey) {
            	//dispatch remainder of index build and return result
               var t = this;
               setTimeout(function(){t.fillMap(i);},2);
               return i;
            }
         }
         return -1;
      },
      /** removes element for key passed */
      remove : function(k)
      {
         var idx = this.indexOf(k);
         if (idx > -1)
         {
            this.keyIndexMap = {};
            this.values.splice(idx, 1);
         }
      },
      /** sets an entry after it may have been changed */
      set : function(entry) {
         if (entry != null) {
        	 if (this.wrapperFunc != null) {
        		 entry = this.wrapperFunc(entry);
        	 }
        	 var key = this.keyFunction(entry);
        	 var index = this.indexOf(key);
        	 if (index==-1) {
                 this.values.push(entry);
                 this.keyIndexMap[key] = this.values.length - 1;    	 
        	 } else {
       			 this.values[index] = entry;
        	 }
         }
      },
      /** returns this.values.length */
      size : function()
      {
         return this.values.length;
      },
      /** @return array of elements sorted by comparator */
      sort : function(comparator)
      {        
         var result = this.values.slice(0);
         result.sort(comparator);
         return result;
      },
      /** sorts this.values by comparator */
      sortInPlace : function(comparator)
      {        
         this.values.sort(comparator);
      },
      /** KeyedArray.toJSON should provide custom output for JSON.stringify */
      toJSON :function(){
    	return this.values;
      },
   };
   return Constructor;
})();