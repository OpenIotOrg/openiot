function Hash()
{
	this.length = 0;
	this.items = new Array();
	for (var i = 0; i < arguments.length; i += 2) {
		if (typeof(arguments[i + 1]) != 'undefined') {
			this.items[arguments[i]] = arguments[i + 1];
			this.length++;
		}
	}
   
	this.removeItem = function(in_key)
	{
		var tmp_value;
		if (typeof(this.items[in_key]) != 'undefined') {
			this.length--;
			var tmp_value = this.items[in_key];
			delete this.items[in_key];
		}
	   
		return tmp_value;
	}

	this.getItem = function(in_key) {
		return this.items[in_key];
	}

	this.setItem = function(in_key, in_value)
	{
		if (typeof(in_value) != 'undefined') {
			if (typeof(this.items[in_key]) == 'undefined') {
				this.length++;
			}

			this.items[in_key] = in_value;
		}
	   
		return in_value;
	}

	this.hasItem = function(in_key)
	{
		return typeof(this.items[in_key]) != 'undefined';
	}
}

//----- (c)GPL, apv
String.prototype.ucFirst = function () {
   return this.substr(0,1).toUpperCase() + this.substr(1,this.length);
}

String.prototype.ucFirstAll = function () {
	var intString = this.substr(0,1).toUpperCase() + this.substr(1,this.length);
  for(var k=1; k < intString.length; k++)
  {
    if(intString[k-1] == ' '){
     	intString = intString.substr(0,k)+intString.substr(k,1).toUpperCase()+intString.substr(k+1,intString.length);
    }
  }
  return intString;
}

String.prototype.removeUnderscoreAndDash = function () {
	var intString = this;
  for(var k=0; k < intString.length; k++)
  {
    if(intString[k] == '-' || intString[k] == '_'){
     	intString = intString.substr(0,k)+" "+intString.substr(k+1,intString.length);
    }
  }
  return intString;
}

String.prototype.prettyString = function () {
  return this.removeUnderscoreAndDash().ucFirstAll();
}