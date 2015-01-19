	.text
	.globl main
	main: #QTSPIM will automatically look for main

	subu $sp $sp 4
	sw $ra ($sp)		#save the ra

	li $v0 7			
	subu $sp $sp 4
	sw $v0 ($sp)		#load first argument to stack

	jal procfact		#execute

	lw $v0 ($sp)		#pop return value from top of stack
	addu $sp $sp 4

	lw $t0 ($sp)
	addu $sp $sp 4		#pop argument from stack

	lw $ra ($sp)		#get the return address back
	addu $sp $sp 4

	move $a0 $v0
	li $v0 1
	syscall				#print out the return value

	li $v0 10
	syscall # halt
procfact:

	#allocate space for return value
	li $t0 0
	subu $sp $sp 4
	sw $t0 ($sp)

	#load lone argument to t1
	addu $t0 $sp 4
	lw $t1 ($t0)
	

	# should t1 be equal to or less than 1 go to endrecur
	beq $t1 0 endrecur
	# otherwise, go to dorecur
	
	#save the ra
	subu $sp $sp 4
	sw $ra ($sp)


	#save the argument which is (t1 - 1)
	subu $t2 $t1 1
	subu $sp $sp 4
	sw $t2 ($sp)

	jal procfact	#execute

	lw $v0 ($sp)
	addu $sp $sp 4	#put return value in v0

	lw $t0 ($sp)
	addu $sp $sp 4	#pop arguments

	lw $ra ($sp)
	addu $sp $sp 4	#put ra back

	addu $t0 $sp 4
	lw $t1 ($t0)	#get 1 again
	
	mult $t1 $v0	#multiply
	mflo $a0	




	sw $a0 ($sp)		#save return value


	#go to ra
	jr $ra
endrecur:
	#set return value (which is $sp) to 1
	li $a0 1
	sw $a0 ($sp)

	#go to ra
	jr $ra
.data
newline:
	.asciiz "\n"
