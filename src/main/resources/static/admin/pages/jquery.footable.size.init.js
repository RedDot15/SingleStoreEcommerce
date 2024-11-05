/**
 * Theme: Frogetor - Responsive Bootstrap 4 Admin Dashboard
 * Author: Mannatthemes
 * Footable Js
 */


$(function () {
    "use strict";

    /*Init FooTable*/
    $('#footable-1,#footable-2').footable();

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
                    $editorTitle.text('Create a new size');

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
                        html: "This will also delete every of product detail relate to this size",
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
                                url: "/admin/size/" + row.val().id + "/delete",
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
                                'Your size is safe :)',
                                'error'
                            )
                        }
                    });
                }
            }
        });

    $editor.on('submit', function (e) {
        if (this.checkValidity && !this.checkValidity()) return;
        e.preventDefault();
        var row = $modal.data('row'),
            values = {
                id: $editor.find('#id').val(),
                name: $editor.find('#name').val()
            };

        $.ajax({
            type: "POST",
            url: "/admin/size/save",
            contentType: 'application/json',
            data: JSON.stringify(values),
        }).done(function (res) {
            if (row instanceof FooTable.Row) {
                row.val(values);
            } else {
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