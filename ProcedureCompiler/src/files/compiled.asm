	.text
	.globl main
	main: #QTSPIM will automatically look for main
	subu $sp $sp 4
	sw $ra ($sp)
	li $v0 5
	subu $sp $sp 4
	sw $v0 ($sp)
	jal procfact
	lw $t0 ($sp)
	addu $sp $sp 4
	lw $ra ($sp)
	addu $sp $sp 4
	la $t0 varr
	sw $v0 ($t0)
	la $t0 varr
	lw $v0 ($t0)
	move $a0 $v0
	li $v0 1
	syscall
	li $v0 4
	la $a0 newline
	syscall
	li $v0 10
	syscall # halt
procfact:
	li $v0 0
	subu $sp $sp 4
	sw $v0 ($sp)
	addu $t0 $sp 4
	lw $v0 ($t0)
	move $a0 $v0
	li $v0 1
	bne $a0 $v0 endif1
	li $v0 1
	addu $t0 $sp 0
	sw $v0 ($t0)
endif1:
	addu $t0 $sp 4
	lw $v0 ($t0)
	move $a0 $v0
	li $v0 1
	beq $a0 $v0 endif2
	subu $sp $sp 4
	sw $ra ($sp)
	addu $t0 $sp 8
	lw $v0 ($t0)
	subu $sp $sp 4
	sw $v0 ($sp)
	li $v0 1
	lw $t0 ($sp)
	addu $sp $sp 4
	subu $v0 $t0 $v0
	subu $sp $sp 4
	sw $v0 ($sp)
	jal procfact
	lw $t0 ($sp)
	addu $sp $sp 4
	lw $ra ($sp)
	addu $sp $sp 4
	la $t0 varr
	sw $v0 ($t0)
	addu $t0 $sp 4
	lw $v0 ($t0)
	subu $sp $sp 4
	sw $v0 ($sp)
	la $t0 varr
	lw $v0 ($t0)
	lw $t0 ($sp)
	addu $sp $sp 4
	mult $t0 $v0
	mflo $v0
	addu $t0 $sp 0
	sw $v0 ($t0)
endif2:
	lw $v0 ($sp)
	addu $sp $sp 4
	jr $ra
	.data
varr:
	.word 0
newline:
	.asciiz "\n"
