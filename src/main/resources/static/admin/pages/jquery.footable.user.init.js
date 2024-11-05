/**
 * Theme: Frogetor - Responsive Bootstrap 4 Admin Dashboard
 * Author: Mannatthemes
 * Footable Js
 */


$(function () {
    "use strict";

    /*Init FooTable*/
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
                    $editorTitle.text('Add a new row');

					$editor.find('#password').attr("required",true);

                    $modal.modal('show');
					setTimeout(function () {
						$('#username').focus();
					}, 500);
                },
                editRow: function (row) {
					$editor.find('#password').val('').removeAttr('required');

                    var values = row.val();
                    $editor.find('#id').val(values.id);
                    $editor.find('#username').val(values.username);
                    $editor.find('#role').val(values.role);
                    $editor.find('#email').val(values.email);
                    $editor.find('#phoneNumber').val(values.phoneNumber);
                    $editor.find('#address').val(values.address);

                    $modal.data('row', row);
                    $editorTitle.text('Edit row #' + values.id);
                    $modal.modal('show');
                },
                deleteRow: function (row) {
					swal({
						title: 'Are you sure?',
						text: "You won't be able to revert this!",
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
								url: "/admin/user/" + row.val().id + "/delete",
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
								'Your user is safe :)',
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
		let $checkbox = $('#footable-3 input[type="checkbox"]');

		$checkbox.on('click', function (e) {
			e.preventDefault();

			let id = $(this).closest('tr').find('td:first').text();

			let $checkboxClicked = $(this);

			if (!this.checked){
				swal({
					title: 'Are you sure you want to deactivate this user?',
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
							url: "/admin/user/" + id + "/deactivate"
						}).done(function (res){
							swal(
								'Deactivate!',
								res.message,
								'success'
							)
							$checkboxClicked.prop('checked', false);
						})
					} else {
						// This function will run if the user clicked "cancel"
						swal(
							'Cancelled!',
							'Your user remain active :)',
							'error'
						)
					}
				});
			}
			else{
				swal({
					title: 'Are you sure?',
					text: "This action will activate this user!",
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
							url: "/admin/user/" + id + "/activate"
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
							'Your user remain inactive :)',
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
				username: $editor.find('#username').val(),
				password: $editor.find('#password').val(),
				role: $editor.find('#role').val(),
				email: $editor.find('#email').val(),
			 	phoneNumber: $editor.find('#phoneNumber').val(),
				address: $editor.find('#address').val()
			};

		$.ajax({
			type: "POST",
			url: "/admin/user/save",
			contentType: 'application/json',
			data: JSON.stringify(values),
		}).done(function (res) {
			if (row instanceof FooTable.Row) {
				row.val(values);
			} else {
				res.data.isActive = '<div class="checkbox checkbox-primary checkbox-single">\n' +
					'                  <input type="checkbox" id="singleCheckbox2" value="option2" aria-label="Single checkbox Two" checked="checked">\n' +
					'                  <label></label>\n' +
					'                </div>';
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