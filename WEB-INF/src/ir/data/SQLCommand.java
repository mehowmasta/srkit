/**
 * 
 */
package ir.data;

public enum SQLCommand {
  Delete(BindFields.No, BindAuto.No, BindKeys.Yes),
  Exists(BindFields.No, BindAuto.No, BindKeys.Yes),
  Insert(BindFields.Yes, BindAuto.No, BindKeys.No),
  InsertNextVal(BindFields.Yes, BindAuto.No, BindKeys.No),
  InsertNoGen(BindFields.Yes, BindAuto.Yes, BindKeys.No),
  Select(BindFields.No,BindAuto.No, BindKeys.Yes),
  Update(BindFields.Yes, BindAuto.No, BindKeys.Yes);
  private BindAuto bindAuto;
  private BindFields bindFields;
  private BindKeys bindKeys;
  private SQLCommand(BindFields bindFields, BindAuto bindAuto, BindKeys bindKeys)
  {
    this.bindFields = bindFields;
    this.bindAuto = bindAuto;
    this.bindKeys = bindKeys;
  }
  public boolean bindAuto()
  {
    return bindAuto.yes();
  }
  public boolean bindFields()
  {
    return bindFields.yes();
  }
  public boolean bindKeys()
  {
    return bindKeys.yes();
  }
  enum BindAuto {Yes,No;public boolean yes(){return this==Yes;}}
  enum BindFields{Yes,No;public boolean yes(){return this==Yes;}}
  enum BindKeys{Yes,No;public boolean yes(){return this==Yes;}}
}