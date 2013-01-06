/*
 * File: app/view/AssayForm.js
 * Date: Wed May 02 2012 15:50:50 GMT-0400 (Eastern Daylight Time)
 *
 * This file was generated by Sencha Architect version 2.0.0.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.0.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.0.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

Ext.define('BARD.view.AssayForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.assayform',

    height: 602,
    id: '',
    width: 938,
    layout: {
        type: 'absolute'
    },
    bodyPadding: 10,
    preventHeader: true,
    title: 'AssayForm',
    url: 'api/assay',

    initComponent: function() {
        var me = this;

        me.initialConfig = Ext.apply({
            url: 'api/assay'
        }, me.initialConfig);

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'textfield',
                    width: 240,
                    name: 'assayId',
                    fieldLabel: 'ID #'
                },
                {
                    xtype: 'combobox',
                    width: 230,
                    name: 'assayStatus',
                    fieldLabel: 'Status',
                    allowBlank: false,
                    editable: false,
                    displayField: 'status',
                    forceSelection: true,
                    store: 'AssayStatutsStore',
                    valueField: 'id',
                    x: 320,
                    y: 10
                },
                {
                    xtype: 'datefield',
                    width: 260,
                    name: 'lastUpdated',
                    fieldLabel: 'Last Updated',
                    x: 600,
                    y: 10
                },
                {
                    xtype: 'textfield',
                    width: 540,
                    name: 'assayName',
                    fieldLabel: 'Name',
                    x: 10,
                    y: 40
                },
                {
                    xtype: 'textfield',
                    width: 260,
                    name: 'assayVersion',
                    fieldLabel: 'Version',
                    x: 600,
                    y: 40
                },
                {
                    xtype: 'textfield',
                    width: 310,
                    name: 'designedBy',
                    fieldLabel: 'Biologist',
                    x: 10,
                    y: 70
                },
                {
                    xtype: 'textareafield',
                    height: 140,
                    width: 850,
                    name: 'description',
                    fieldLabel: 'Description',
                    x: 10,
                    y: 100
                },
                {
                    xtype: 'gridpanel',
                    height: 190,
                    width: 210,
                    title: 'Protocol',
                    store: 'assayStore',
                    x: 10,
                    y: 280,
                    columns: [
                        {
                            xtype: 'gridcolumn',
                            width: 209,
                            dataIndex: 'string',
                            text: 'Protocol Links'
                        }
                    ],
                    viewConfig: {

                    }
                },
                {
                    xtype: 'gridpanel',
                    height: 190,
                    width: 420,
                    title: 'Project',
                    store: 'assayStore',
                    x: 340,
                    y: 280,
                    viewConfig: {

                    },
                    columns: [
                        {
                            xtype: 'gridcolumn',
                            width: 230,
                            dataIndex: 'string',
                            text: 'Name'
                        },
                        {
                            xtype: 'gridcolumn',
                            width: 89,
                            text: 'Type'
                        },
                        {
                            xtype: 'gridcolumn',
                            text: 'Stage'
                        }
                    ]
                },
                {
                    xtype: 'button',
                    text: 'Add Protocol',
                    x: 230,
                    y: 280
                },
                {
                    xtype: 'button',
                    text: 'Del Protocol',
                    x: 230,
                    y: 310
                },
                {
                    xtype: 'button',
                    width: 90,
                    text: 'Add Project',
                    x: 770,
                    y: 280,
                    listeners: {
                        click: {
                            fn: me.onAddProjectButtonClick,
                            scope: me
                        }
                    }
                },
                {
                    xtype: 'button',
                    text: 'Remove Project',
                    x: 770,
                    y: 310
                },
                {
                    xtype: 'button',
                    text: 'Submit / Next',
                    x: 810,
                    y: 510,
                    listeners: {
                        click: {
                            fn: me.onNextButtonClick,
                            scope: me
                        }
                    }
                },
                {
                    xtype: 'button',
                    height: 20,
                    width: 60,
                    text: 'Cancel',
                    x: 730,
                    y: 510,
                    listeners: {
                        click: {
                            fn: me.onCancelButtonClick,
                            scope: me
                        }
                    }
                }
            ]
        });

        me.callParent(arguments);
    },

    onAddProjectButtonClick: function(button, e, options) {
        Ext.create('BARD.view.ProjectWindow', {modal:true}).show();
    },

    onNextButtonClick: function(button, e, options) {
        var assayFormPanel = Ext.getCmp("assayForm");
        var assayForm = assayFormPanel.getForm('form');
        if (assayForm.isValid()) {
            //assayForm.submit();
            var assayStore = Ext.getStore('assayStore');
            var assayModel = Ext.create('BARD.model.Assay');
            assayForm.updateRecord(assayModel);
            assayStore.insert(0, assayModel);
            assayStore.sync();
            Ext.MessageBox.alert('Thank you!', 'Your form has been submitted.');
        }
    },

    onCancelButtonClick: function(button, e, options) {
        var assayFormPanel = Ext.getCmp("assayForm");
        var assayForm = assayFormPanel.getForm('form');
        assayForm.reset();
    }

});