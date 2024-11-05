/**
 * Theme: Frogetor - Responsive Bootstrap 4 Admin Dashboard
 * Author: Mannatthemes
 * Footable Js
 */

$(function () {
    "use strict";

    /*Editing FooTable*/
    var $modal = $('#editor-modal'),
        $editor = $('#editor'),
        $editorTitle = $('#editor-title'),
        ft = FooTable.init('#footable-3', {
            editing: {
                enabled: true,
                addRow: function () {
                    $modal.removeData('row');
                    $editor[0].reset();
                    $editorTitle.text('Create a new category');

                    $modal.modal('show');
                    setTimeout(function () {
                        $('#name').focus();
                    }, 500);
                },
                editRow: function (row) {
                    var values = row.val();
                    $editor.find('#id').val(values.id);
                    $editor.find('#name').val(values.name);

                    $modal.data('row', row);
                    $editorTitle.text('Edit row #' + values.id);
                    $modal.modal('show');
                },
                deleteRow: function (row) {
                    swal({
                        title: 'Are you sure?',
                        text: "You won't be able to revert this!",
                        html: "This will also delete every of its product",
                        type: 'warning',
                        showCancelButton: true,
                        confirmButtonText: 'Yes, delete it!',
                        cancelButtonText: 'No, cancel!',
                        confirmButtonClass: 'btn btn-success',
                        cancelButtonClass: 'btn btn-danger ml-2',
                        buttonsStyling: false
                    }).then((result) => {
                        if (result.value) {
                            // This function will run ONLY if the user clicked Yes!
                            // Only here we want to send the request to the server!
                            $.ajax({
                                type: "Delete",
                                url: "/admin/category/" + row.val().id + "/delete",
                            }).done(function (res) {
                                swal(
                                    'Deleted!',
                                    res.message,
                                    'success'
                                )
                                row.delete();
                            }).fail(function (e) {
                                console.log(e);
                                swal(
                                    'Not found!',
                                    e.responseJSON.message,
                                    'error'
                                )
                            });
                        } else {
                            // This function will run if the user clicked "cancel"
                            swal(
                                'Cancelled!',
                                'Your category is safe :)',
                                'error'
                            )
                        }
                    });
                }
            }
        }, function (ft){
            addEventToCheckbox();
        });

    $('#footable-3').on('after.ft.paging', function () {
        addEventToCheckbox();
    });

    $('#footable-3').on('after.ft.filtering', function () {
        addEventToCheckbox();
    });

    function addEventToCheckbox() {
        var $checkbox = $('#footable-3 input[type="checkbox"]');

        $checkbox.on('click', function (e) {
            e.preventDefault();

            let id = $(this).closest('tr').find('td:first').text();

            let $checkboxClicked = $(this);

            if (!this.checked){
                swal({
                    title: 'Are you sure you want to deactivate this category?',
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, deactivate it!',
                    cancelButtonText: 'No, cancel!',
                    confirmButtonClass: 'btn btn-success',
                    cancelButtonClass: 'btn btn-danger ml-2',
                    buttonsStyling: false
                }).then((result) => {
                    if (result.value) {
                        // This function will run ONLY if the user clicked Yes!
                        // Only here we want to send the request to the server!
                        $.ajax({
                            type: "PUT",
                            url: "/admin/category/" + id + "/deactivate"
                        }).done(function (res){
                            swal(
                                'Deactivate!',
                                res.message,
                                'success'
                            )
                            $checkboxClicked.prop('checked', false);
                        }).fail(function (res){
                            swal(
                                'Something is wrong!',
                                res.responseJSON.message,
                                'error'
                            )
                        })
                    } else {
                        // This function will run if the user clicked "cancel"
                        swal(
                            'Cancelled!',
                            'Your category remain active :)',
                            'error'
                        )
                    }
                });
            }
            else{
                swal({
                    title: 'Are you sure?',
                    text: "This action will activate this category!",
                    type: 'warning',
                    showCancelButton: true,
                    confirmButtonText: 'Yes, activate it!',
                    cancelButtonText: 'No, cancel!',
                    confirmButtonClass: 'btn btn-success',
                    cancelButtonClass: 'btn btn-danger ml-2',
                    buttonsStyling: false
                }).then((result) => {
                    if (result.value) {
                        // This function will run ONLY if the user clicked Yes!
                        // Only here we want to send the request to the server!
                        $.ajax({
                            type: "PUT",
                            url: "/admin/category/" + id + "/activate"
                        }).done(function (res){
                            swal(
                                'Activate!',
                                res.message,
                                'success'
                            )
                            $checkboxClicked.prop('checked', true);
                        })
                    } else {
                        // This function will run if the user clicked "cancel"
                        swal(
                            'Cancelled!',
                            'Your category remain inactive :)',
                            'error'
                        )
                    }
                });
            }
        })
    }

    $editor.on('submit', function (e) {
        if (this.checkValidity && !this.checkValidity()) return;
        e.preventDefault();
        var row = $modal.data('row'),
            values = {
                id: $editor.find('#id').val(),
                name: $editor.find('#name').val(),
            };

        $.ajax({
            type: "POST",
            url: "/admin/category/save",
            contentType: 'application/json',
            data: JSON.stringify(values),
        }).done(function (res) {
            if (row instanceof FooTable.Row) {
                row.val(values);
            } else {
                res.data.isActive = '<div class="checkbox checkbox-primary checkbox-single">\n' +
                    '                  <input type="checkbox" id="singleCheckbox2" value="option2" aria-label="Single checkbox Two" checked="checked">\n' +
                    '                  <label></label>\n' +
                    '                </div>'
                ft.rows.add(res.data);
            }
            swal({
                title: 'Success!',
                text: res.message,
                type: 'success',
                showCancelButton: false,
                confirmButtonClass: 'btn btn-success'
            });
        }).fail(function (error) {
            swal({
                type: "error",
                title: "Oops...",
                text: error.responseJSON.message,
            });
        });
        $modal.modal('hide');
    });

});