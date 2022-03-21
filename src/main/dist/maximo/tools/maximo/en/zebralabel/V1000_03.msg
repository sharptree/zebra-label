<?xml version="1.0" encoding="UTF-8"?>
<!--suppress CheckTagEmptyBody -->
<messages cleanup="false" >
    <Message id="STXAB0001E" group="sharptree" key="invalidHost" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>{0} is not a valid host name or IP address.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0002E" group="sharptree" key="invalidPort" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>{0} is not a valid port number, port numbers must be between 0 and 65535.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0003E" group="sharptree" key="invalidStoreroom" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>{1} is not a valid storeroom.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0004I" group="sharptree" key="changeDefaultLabel" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNYES</Option>
            <Option>MSG_BTNNO</Option>
            <Option>MSG_BTNCANCEL</Option>
        </Display>
        <MsgText>Another label is already declared as the default for the {0} media, do you want to change the default label?</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0005I" group="sharptree" key="labelPrinted" prefix="1">
        <Display method="STATUS">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>Label for item {0} printed.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0005E" group="sharptree" key="printError" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>Error Printing Label:\n{0}.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0006E" group="sharptree" key="invalidZPL" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>The entered ZPL is invalid, it must start with ^XA and end with ^XZ.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0007E" group="sharptree" key="countLessThanOne" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>The label count cannot be less than one.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0008E" group="sharptree" key="countGreaterThanMax" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>The label count cannot be greater than {1}.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0009E" group="sharptree" key="noToStoreLoc" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>Labels can only be printed for items received to a storeroom.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0010I" group="sharptree" key="changeDefaultPrinter" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNYES</Option>
            <Option>MSG_BTNNO</Option>
            <Option>MSG_BTNCANCEL</Option>
        </Display>
        <MsgText>Another printer is already declared as the default for the {0} store, do you want to change the default printer?</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
    <Message id="STXAB0011E" group="sharptree" key="unknownPrintError" prefix="1">
        <Display method="MSGBOX">
            <Option>MSG_BTNOK</Option>
        </Display>
        <MsgText>An unexpected printing error has occurred: {0}.</MsgText>
        <Explanation></Explanation>
        <AdminResponse></AdminResponse>
        <OperatorResponse></OperatorResponse>
    </Message>
</messages>