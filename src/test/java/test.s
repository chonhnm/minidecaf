	.text
	.globl	main
main:
	addi sp, sp, -12
	sw ra, 8(sp)
	sw fp, 4(sp)
	addi fp, sp, 12
	li t1,0
	addi sp,sp,-4
	sw t1, 0(sp)
	addi sp, sp, -4
	addi t1, fp, -12
	sw t1, 0(sp)
	lw t1, 4(sp)
	lw t2, 0(sp)
	addi sp, sp, 4
	sw t1, 0(t2)
	addi sp, sp, 4
	li t1,3
	addi sp,sp,-4
	sw t1, 0(sp)
	addi sp, sp, -4
	addi t1, fp, -12
	sw t1, 0(sp)
	lw t1, 4(sp)
	lw t2, 0(sp)
	addi sp, sp, 4
	sw t1, 0(t2)
	addi sp, sp, 4
	addi sp, sp, -4
	addi t1, fp, -12
	sw t1, 0(sp)
	lw t1, 0(sp)
	lw t1, 0(t1)
	sw t1, 0(sp)
	jal t1, main_epilogue
main_epilogue:
	lw a0, 0(sp)
	addi sp, sp, 4
	lw fp, 4(sp)
	lw ra, 8(sp)
	addi sp, sp, 12
	jr ra
